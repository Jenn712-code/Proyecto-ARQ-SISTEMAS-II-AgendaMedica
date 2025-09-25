package recursos;

import entidades.Cita;
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
    @Path("/crear")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Cita crearCita(Cita cita) {
        return citaServicios.crear(cita);
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cita> obtenerCitas() {
        //para listar todas las citas
        return citaServicios.listarCitas();
    }
}
