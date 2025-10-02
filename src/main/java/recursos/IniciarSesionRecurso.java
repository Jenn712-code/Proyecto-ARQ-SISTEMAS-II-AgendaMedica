package recursos;

import dto.IniciarSesionDTO;
import entidades.Paciente;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import servicios.IniciarSesionServicio;

import java.util.Map;

@Path("/IniciarSesion")
@AllArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IniciarSesionRecurso {

    @Inject
    IniciarSesionServicio iniciarSesionServicio;

    @POST
    @Path("/autenticacion")
    public Response autenticacion(IniciarSesionDTO iniciarSesion) {

        Paciente autenticado = iniciarSesionServicio.autenticacion(
                iniciarSesion.pacCorreo,
                iniciarSesion.pacContrasena
        );

        if (autenticado == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("mensaje", "No se encuentra registrado"))
                    .build();
        }

        return Response.ok(autenticado).build();
    }

    @GET
    @Path("/{correo}")
    public Response getPaciente(@PathParam("correo") String correo) {
        Paciente paciente = Paciente.find("pacCorreo", correo).firstResult();

        if (paciente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Paciente no encontrado")
                    .build();
        }

        return Response.ok(paciente).build();
    }
}