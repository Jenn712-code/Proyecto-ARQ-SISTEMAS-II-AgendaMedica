package recursos;

import dto.MedicamentoDTO;
import entidades.Medicamento;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import servicios.MedicamentoServicio;

import java.util.List;

@Path("/medicamentos")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicamentoRecurso {

    @Inject
    MedicamentoServicio medicamentoServicio;
    @POST
    @Path("/crearMedicamento")

    public Response crearMedicamento(MedicamentoDTO dto) {

        if (dto.medEstado == null || dto.medEstado.isBlank()) {
            dto.medEstado = "pendiente";
        }

        if(dto.medNombre == null || dto.medDosis == null || dto.medFrecuencia == null
                || dto.medDuracion == null ||dto.pacCedula == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos incompletos")
                    .build();
        }
        Medicamento medicamento = medicamentoServicio.crearMedicamento(dto);

        if (medicamento.getMedId() != null && medicamento.getMedId() == -1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El paciente no existe")
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(medicamento).build();
    }

    @GET
    @Path("/listarMedicamentos/{pacCedula}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Medicamento> obtenerCitas(@PathParam("pacCedula") Integer cedula) {
        return medicamentoServicio.listarMedicamentosPorPaciente(cedula);
    }
}

