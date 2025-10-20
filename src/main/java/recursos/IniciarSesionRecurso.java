package recursos;

import dto.IniciarSesionDTO;
import entidades.Paciente;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import seguridad.TokenUtils;
import servicios.IniciarSesionServicio;
import io.smallrye.jwt.auth.principal.JWTParser;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@Path("/IniciarSesion")
@AllArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings("java:S3252") // SonarLint: Static access for PanacheEntityBase
public class IniciarSesionRecurso {

    IniciarSesionServicio iniciarSesionServicio;
    Mailer mailer;
    JWTParser jwtParser;

    private static final String PAC_CORREO = "pacCorreo";
    private static final String TOKEN = "token";
    private static final String NUEVA_CONTRASENA  = "nuevaContrasena";
    private static final String MENSAJE = "mensaje";

    @POST
    @Path("/autenticacion")
    public Response autenticacion(IniciarSesionDTO iniciarSesion) {
        Paciente autenticado = iniciarSesionServicio.autenticacion(
                iniciarSesion.pacCorreo,
                iniciarSesion.pacContrasena
        );

        if (autenticado == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(MENSAJE, "No tiene cuenta, por favor registrese"))
                    .build();
        }

        // Generar token JWT
        String token = TokenUtils.generateToken(
                autenticado.getPacCorreo(),
                Set.of("paciente"),
                autenticado.getPacNombre(),
                autenticado.getPacCedula()
        );

        // Retornar el token junto con algunos datos del usuario
        return Response.ok(Map.of(
                TOKEN, token,
                "correo", autenticado.getPacCorreo(),
                "cedula", autenticado.getPacCedula(),
                "nombre", autenticado.getPacNombre()
        )).build();
    }

    @GET
    @Path("/{correo}")
    @RolesAllowed("paciente")
    public Response getPaciente(@PathParam("correo") String correo) {
        Paciente paciente = Paciente.find(PAC_CORREO, correo).firstResult();

        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(MENSAJE, "Paciente no encontrado"))
                    .build();
        }

        return Response.ok(paciente).build();
    }

    @POST
    @Path("/olvide_contrasena")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response olvideContrasena(Map<String, String> body) {
        if (body == null || !body.containsKey(PAC_CORREO)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(MENSAJE, "Correo requerido")).build();
        }

        String correo = body.get(PAC_CORREO);

        Paciente paciente = Paciente.find(PAC_CORREO, correo).firstResult();
        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(MENSAJE, "El correo ingresado no se encuentra registrado")).build();
        }

        // Generar token temporal JWT (expira en 10 minutos)
        String tokenRecuperacion = Jwt.issuer("miapp-issuer")
                .subject(correo)
                .expiresIn(Duration.ofMinutes(5))
                .claim("tipo", "recuperacion")
                .sign();

        // Enviar correo con solo el token
        mailer.send(Mail.withText(
                correo,
                "Recuperación de contraseña",
                "Hola " + paciente.getPacNombre() + ",\n\n" +
                        "Haz solicitado recuperar tu contraseña.\n" +
                        "Tu token de recuperación es:\n\n" + tokenRecuperacion + "\n\n" +
                        "Este token es válido por 5 minutos.\n" +
                        "Ingresa este token en la aplicación para restablecer tu contraseña."
        ));
        return Response.ok(Map.of(MENSAJE, "Se ha enviado un token de recuperación a su correo electrónico registrado")).build();
    }

    @POST
    @Path("/restablecer_contrasena")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response restablecer(Map<String, String> body) {
        if (body == null ||
                !body.containsKey(PAC_CORREO) ||
                !body.containsKey(TOKEN) ||
                !body.containsKey(NUEVA_CONTRASENA )) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(MENSAJE, "Correo, token y nueva contraseña requeridos")).build();
        }

        String correo = body.get(PAC_CORREO);
        String token = body.get(TOKEN);
        String nuevaContrasena = body.get(NUEVA_CONTRASENA );

        try {
            // Validar token
            JsonWebToken jwt = jwtParser.parse(token);

            // Validar que el token no haya expirado
            if (jwt.getExpirationTime() < (System.currentTimeMillis() / 5000)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of(MENSAJE, "Token expirado")).build();
            }

            // Validar que el correo coincida con el del token
            String correoDelToken = jwt.getSubject();
            if (!correo.equals(correoDelToken)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of(MENSAJE, "El token no pertenece a este correo")).build();
            }

            // Validar que sea un token de recuperación
            Object tipo = jwt.getClaim("tipo");
            if (tipo == null || !"recuperacion".equals(tipo.toString())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of(MENSAJE, "El token ingresado es inválido")).build();
            }

            // Buscar paciente
            Paciente paciente = Paciente.find(PAC_CORREO, correo).firstResult();
            if (paciente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of(MENSAJE, "Usuario no encontrado")).build();
            }

            if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
                return Response.ok(Map.of(MENSAJE, "Token validado correctamente. Ahora puede restablecer su contraseña")).build();
            }

            // Actualizar contraseña
            String hash = BcryptUtil.bcryptHash(nuevaContrasena);
            paciente.setPacContrasena(hash);

            return Response.ok(Map.of(MENSAJE, "Contraseña restablecida correctamente. Ahora puede iniciar sesión")).build();

        } catch (ParseException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(MENSAJE, "El token ingresado es inválido, intente de nuevo")).build();
        } catch (Exception e) {
            LOGGER.error("Error al validar el token", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(MENSAJE, "Error interno al procesar la solicitud")).build();
        }
    }


    @GET
    @Path("/test")
    public Response enviarCorreo() {
        try {
            mailer.send(Mail.withText(
                    "agenda.medica.app2025@gmail.com",
                    "Prueba de correo desde Quarkus",
                    "¡Hola! Este es un mensaje de prueba enviado desde Quarkus con Gmail."
            ));
            return Response.ok(Map.of(MENSAJE, "Correo enviado correctamente")).build();
        } catch (Exception e) {
            LOGGER.error("Error al enviar el correo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "No se pudo enviar el correo: " + e.getMessage()))
                    .build();
        }
    }
}