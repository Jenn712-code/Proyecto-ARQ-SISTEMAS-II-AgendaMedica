package recursos;

import dto.MedicamentoDTO;
import entidades.Medicamento;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import seguridad.TokenUtils;
import servicios.MedicamentoServicio;
import java.util.List;

@Path("/medicamentos")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicamentoRecurso {

    MedicamentoServicio medicamentoServicio;
    @POST
    @Path("/crearMedicamento")
    @RolesAllowed({"paciente"})

    public Response crearMedicamento(@Context SecurityContext ctx, MedicamentoDTO dto) {

        try {

            //Obtener la info del usuario autenticado desde el token JWT
            Integer cedula = TokenUtils.obtenerCedulaDesdeToken(ctx);
            if (cedula == null) {
                return TokenUtils.respuestaCedulaNoEncontrada();
            }

            if (dto.medEstado == null || dto.medEstado.isBlank()) {
                dto.medEstado = "pendiente";
            }

            if (dto.medNombre == null || dto.medDosis == null || dto.medFrecuencia == null || dto.medDuracion == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Datos incompletos")
                        .build();
            }

            //Asignar la cédula del paciente autenticado al DTO
            dto.pacCedula = cedula;

            Medicamento medicamento = medicamentoServicio.crearMedicamento(dto);

            return Response.status(Response.Status.CREATED)
                    .entity(medicamento)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear el medicamento: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/listarMedicamentos")
    @RolesAllowed({"paciente"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarMedicamentos(@Context SecurityContext ctx) {
        try {
            //Obtener la cédula del token
            JWTCallerPrincipal jwt = (JWTCallerPrincipal) ctx.getUserPrincipal();
            String cedulaStr = jwt.getClaim("cedula");
            Integer cedula = Integer.parseInt(cedulaStr);

            //Buscar medicamentos del paciente autenticado
            List<Medicamento> medicamentos = medicamentoServicio.listarMedicamentosPorPaciente(cedula);

            return Response.ok(medicamentos).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al listar medicamentos: " + e.getMessage())
                    .build();
        }
    }
}