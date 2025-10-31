package recursos;

import dto.CitaDTO;
import entidades.Cita;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import seguridad.TokenUtils;
import servicios.CitaServicios;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

import java.util.List;

@Path("/citas")
@AllArgsConstructor
public class CitaRecursos {

    private CitaServicios citaServicios;

    @POST
    @Path("/crearCita")
    @RolesAllowed({"paciente"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearCita(@Context SecurityContext ctx, CitaDTO dto) {

        //Obtener la info del usuario autenticado desde el token JWT
        Integer cedula = TokenUtils.obtenerCedulaDesdeToken(ctx);
        if (cedula == null) {
            return TokenUtils.respuestaCedulaNoEncontrada();
        }
        //Asignar la cédula del paciente autenticado al DTO
        dto.pacCedula = cedula;

        Cita cita = citaServicios.crearCita(dto);

        if (dto.citNomMedico == null || dto.citNomMedico.isBlank() || dto.citFecha == null
                || dto.citHora == null || dto.citDireccion == null || dto.citDireccion.isBlank()
                || dto.pacCedula == null || dto.espId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos incompletos")
                    .build();
        }

        if (cita.getCitId() != null && cita.getCitId() == -2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El paciente no existe")
                    .build();
        }

        if (cita.getCitId() != null && cita.getCitId() == -3) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La especialidad no existe")
                    .build();
        }

        if (cita.getCitId() != null && cita.getCitId() == -1) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Ya existe una cita en esa fecha y hora")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(cita).build();
    }

    @GET
    @Path("/listarCitas/{pacCedula}")
    @RolesAllowed({"paciente"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCitas(@PathParam("pacCedula") Integer pacCedula, @Context SecurityContext ctx) {
        Integer cedulaToken = TokenUtils.obtenerCedulaDesdeToken(ctx);

        if (cedulaToken == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se pudo obtener la cédula del token")
                    .build();
        }

        // Si el usuario autenticado solo puede ver sus propias citas:
        if (!cedulaToken.equals(pacCedula)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para ver las citas de otro paciente")
                    .build();
        }

        List<Cita> citas = citaServicios.listarCitasPorPaciente(pacCedula);
        return Response.ok(citas).build();
    }
}
