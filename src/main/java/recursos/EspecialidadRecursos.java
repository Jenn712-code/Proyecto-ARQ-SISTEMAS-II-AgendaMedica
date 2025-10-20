package recursos;

import entidades.Especialidad;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import servicios.EspecialidadServicio;
import java.util.List;

@Path("/especialidades")
@AllArgsConstructor
public class EspecialidadRecursos {

    private EspecialidadServicio especialidadServicio;

    @GET
    @Path("/listarEspecialidades")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Especialidad> obtenerEspecialidades() {
        //para listar todas las especialidades
        return especialidadServicio.listarEspecialidades();
    }
}
