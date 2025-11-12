package servicios;

import dto.MedicamentoDTO;
import entidades.Medicamento;
import entidades.Paciente;
import entidades.TipoServicio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import repositorios.MedicamentoRepositorio;

import java.util.List;

@ApplicationScoped
@SuppressWarnings("java:S3252")
@AllArgsConstructor
public class MedicamentoServicio {

    MedicamentoRepositorio medicamentoRepositorio;
    NotificacionServicios notificacionServicios;

    @Transactional
    public Medicamento crearMedicamento(MedicamentoDTO dto){
        Paciente paciente = Paciente.findById(dto.pacCedula);
        if (paciente == null) {
            Medicamento dummy = new Medicamento();
            dummy.setMedId(-1);
            return dummy;
        }

        Medicamento medicamento = new Medicamento();
        medicamento.setMedNombre(dto.medNombre);
        medicamento.setMedDosis(dto.medDosis);
        medicamento.setMedFrecuencia(dto.medFrecuencia);
        medicamento.setMedDuracion(dto.medDuracion);
        TipoServicio tipo = TipoServicio.find("tipNombre", "Medicamento").firstResult();
        medicamento.setTipoServicio(tipo);
        medicamento.setMedFecha(dto.medFecha);
        if (dto.medEstado == null || dto.medEstado.isBlank()) {
            medicamento.setMedEstado("Pendiente");
        } else {
            medicamento.setMedEstado(dto.medEstado);
        }
        medicamento.setMedRecordatorio(dto.medRecordatorio);
        medicamento.setPaciente(paciente);

        medicamentoRepositorio.persist(medicamento);
        medicamentoRepositorio.flush();

        // asignar el ID generado al DTO antes de llamar a notificacionServicios
        dto.medId = medicamento.getMedId();
        notificacionServicios.generarNotificacionesParaMedicamento(dto);

        return medicamento;
    }

    public List<Medicamento> listarMedicamentosPorPaciente(Integer pacCedula) {
        return medicamentoRepositorio.list("paciente.pacCedula", pacCedula);
    }
}

