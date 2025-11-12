package recursos;

import dto.NotificacionDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import seguridad.TokenUtils;
import servicios.NotificacionServicios;
import java.util.List;

@Path("/notificaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class NotificacionRecurso {

    NotificacionServicios notificacionServicio;

    @GET
    @Path("/pendientes")
    @RolesAllowed({"paciente"})
    public Response obtenerNotificacionesPendientes(@Context SecurityContext ctx) {
        Integer cedula = TokenUtils.obtenerCedulaDesdeToken(ctx);
        if (cedula == null) {
            return TokenUtils.respuestaCedulaNoEncontrada();
        }

        List<NotificacionDTO> notificaciones =
                notificacionServicio.obtenerNotificacionesPendientesPorCedula(cedula);

        if (notificaciones.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(notificaciones).build();
    }
}