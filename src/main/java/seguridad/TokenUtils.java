package seguridad;
import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.ws.rs.core.SecurityContext;
import java.time.Duration;
import java.util.Set;

public class TokenUtils {
    private TokenUtils() {
    }

    /**
     * Genera un token JWT firmado usando la clave privada configurada en application.properties.
     *
     * @param correo  Correo del usuario (UPN en el JWT)
     * @param roles   Conjunto de roles o grupos (por ejemplo, "paciente", "admin")
     * @param nombre  Nombre del usuario (se agrega como claim opcional)
     * @return Token JWT firmado
     */
    public static String generateToken(String correo, Set<String> roles, String nombre, Integer cedula) {
        return Jwt.issuer("miapp-issuer")        // debe coincidir con mp.jwt.verify.issuer
                .upn(correo)                               // identificador del usuario
                .groups(roles)                             // roles del usuario
                .claim("nombre", nombre)
                .claim("cedula",cedula)             // claim personalizado opcional
                .expiresIn(Duration.ofHours(1))            // tiempo de expiración
                .sign();                                   // firma el token con privateKey.pem
    }

    public static Integer obtenerCedulaDesdeToken(@Context SecurityContext ctx) {
        try {
            JWTCallerPrincipal jwt = (JWTCallerPrincipal) ctx.getUserPrincipal();
            Object cedulaClaim = jwt.getClaim("cedula");

            if (cedulaClaim == null) {
                return null;
            }

            if (cedulaClaim instanceof Number number) {
                return number.intValue();
            } else {
                return Integer.parseInt(String.valueOf(cedulaClaim));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve una respuesta HTTP de error si el token no contiene la cédula.
     */
    public static Response respuestaCedulaNoEncontrada() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("El token no contiene la cédula del paciente")
                .build();
    }

    public static String obtenerNombreDesdeToken(@Context SecurityContext ctx) {
        try {
            JWTCallerPrincipal jwt = (JWTCallerPrincipal) ctx.getUserPrincipal();
            Object nombreClaim = jwt.getClaim("nombre");

            if (nombreClaim == null) {
                return null;
            }

            if (nombreClaim instanceof String string) {
                return string;
            } else {
                return (String.valueOf(nombreClaim));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve una respuesta HTTP de error si el token no contiene la cédula.
     */
    public static Response respuestaNombreNoEncontrada() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("El token no contiene el nombre del paciente")
                .build();
    }
}