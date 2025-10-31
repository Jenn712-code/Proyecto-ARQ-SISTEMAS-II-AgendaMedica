package recursos;

import dto.RecordatorioDTO;
import entidades.Recordatorio;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import seguridad.TokenUtils;
import servicios.RecordatorioServicio;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/recordatorios")
@AllArgsConstructor
public class RecordatorioRecursos {

    private RecordatorioServicio recordatorioServicio;
    private static final Logger LOGGER = Logger.getLogger(RecordatorioRecursos.class.getName());
    private static final String MENSAJE = "mensaje";

    @POST
    @Path("/configurarRecordatorio")
    @RolesAllowed({"paciente"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response configurarRecordatorio(@Context SecurityContext ctx, RecordatorioDTO dto) {
        try {
            //Obtener la info del usuario autenticado desde el token JWT
            Integer cedula = TokenUtils.obtenerCedulaDesdeToken(ctx);
            if (cedula == null) {
                return TokenUtils.respuestaCedulaNoEncontrada();
            }
            Recordatorio recordatorio = recordatorioServicio.configurarRecordatorio(dto, cedula);
            return Response.status(Response.Status.CREATED).entity(recordatorio).build();
        } catch (IllegalArgumentException e) {
            // Error si el tipo de servicio no existe
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar el método", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al guardar el recordatorio.")
                    .build();
        }
    }

    @GET
    @Path("/cargarRecordatorio/{tipoServicioId}/{cedula}")
    @RolesAllowed({"paciente"})
    public Response obtenerRecordatorio(@PathParam("tipoServicioId") Long tipoServicioId,
                                        @PathParam("cedula") String cedula) {
        try {
            Map<String, Object> recordatorio = recordatorioServicio.obtenerRecordatorio(tipoServicioId, cedula);

            if (recordatorio == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of(MENSAJE, "No se encontró un recordatorio configurado."))
                        .build();
            }

            return Response.ok(recordatorio).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(MENSAJE, e.getMessage()))
                    .build();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar el método", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(MENSAJE, "Error al obtener el recordatorio: " + e.getMessage()))
                    .build();
        }
    }
}