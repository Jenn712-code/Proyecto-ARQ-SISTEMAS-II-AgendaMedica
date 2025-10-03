package servicios;

import dto.MedicamentoDTO;
import entidades.Medicamento;
import entidades.Paciente;
import entidades.TipoServicio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import repositorios.MedicamentoRepositorio;

import java.util.List;

@ApplicationScoped
public class MedicamentoServicio {
    
    @Inject
    MedicamentoRepositorio medicamentoRepositorio;

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
        TipoServicio tipo = TipoServicio.find("tipnombre", "Medicamento").firstResult();
        medicamento.setTipoServicio(tipo);
        medicamento.setMedFecha(dto.medFecha);
        if (dto.medEstado == null || dto.medEstado.isBlank()) {
            medicamento.setMedEstado("pendiente");
        } else {
            medicamento.setMedEstado(dto.medEstado);
        }
        medicamento.setMedRecordatorio(dto.medRecordatorio);
        medicamento.setPaciente(paciente);

        medicamentoRepositorio.persist(medicamento);
        return medicamento;

    }

    public List<Medicamento> listarMedicamentosPorPaciente(Integer pacCedula) {
        return medicamentoRepositorio.list("paciente.pacCedula", pacCedula);
    }
}

