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
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cita> obtenerCitas(@PathParam("pacCedula") Integer cedula) {
        //para listar todas las citas
        return citaServicios.listarCitasPorPaciente(cedula);
    }
}
