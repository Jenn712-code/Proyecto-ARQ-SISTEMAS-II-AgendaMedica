package recursos;

import dto.CitaDTO;
import entidades.Cita;
import jakarta.ws.rs.core.Response;
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearCita(CitaDTO dto) {
        Cita cita = citaServicios.crearCita(dto);

        if (cita == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Paciente o especialidad no existen")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(cita).build();
    }

    @GET
    @Path("/listarCitas/{pacCedula}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cita> obtenerCitas(@PathParam("pacCedula") Integer cedula) {
        //para listar todas las citas
        return citaServicios.listarCitasPorPaciente(cedula);
    }
}
