package recursos;

import dto.CitaDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Response obtenerCitas(@Context SecurityContext ctx) {
        Integer cedulaToken = TokenUtils.obtenerCedulaDesdeToken(ctx);

        if (cedulaToken == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se pudo obtener la cédula del token")
                    .build();
        }

        Map<String, List<MedicamentoDTO>> medicamentosCategorizados = new HashMap<>();
        medicamentosCategorizados.put("pendientes", medicamentoServicio.listarMedicamentosPorEstado(cedulaToken, "Pendiente"));
        medicamentosCategorizados.put("consumidos", medicamentoServicio.listarMedicamentosPorEstado(cedulaToken, "Consumido"));
        medicamentosCategorizados.put("noConsumidos", medicamentoServicio.listarMedicamentosPorEstado(cedulaToken, "No consumido"));

        return Response.ok(medicamentosCategorizados).build();
    }

    @PUT
    @Path("/actualizar")
    @RolesAllowed({"paciente"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarMedicamento(MedicamentoDTO dto, @Context SecurityContext ctx) {

        Integer cedulaToken = TokenUtils.obtenerCedulaDesdeToken(ctx);
        if (cedulaToken == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se pudo obtener la cédula del token")
                    .build();
        }

        if (!cedulaToken.equals(dto.pacCedula)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permiso para modificar este medicamento")
                    .build();
        }

        Medicamento medActualizado = medicamentoServicio.actualizarMedicamento(dto);

        switch (medActualizado.getMedId()) {
            case -2:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Paciente no encontrado").build();
            case -4:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("El medicamento no existe").build();
        }

        return Response.ok(medActualizado).build();
    }

    @DELETE
    @Path("/eliminar/{medId}")
    @RolesAllowed({"paciente"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarMedicamento(@PathParam("medId") Integer medId,
                                        @Context SecurityContext ctx) {

        Integer cedulaToken = TokenUtils.obtenerCedulaDesdeToken(ctx);
        if (cedulaToken == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se pudo obtener la cédula del token")
                    .build();
        }

        Medicamento med = Medicamento.findById(medId);
        if (med == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El medicamento no existe").build();
        }

        if (!med.getPaciente().getPacCedula().equals(cedulaToken)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permiso para eliminar este medicamento")
                    .build();
        }

        Medicamento eliminado = medicamentoServicio.eliminarMedicamento(medId);

        if (eliminado.getMedId() == -4) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El medicamento no existe").build();
        }

        return Response.ok("Medicamento eliminado correctamente").build();
    }
}