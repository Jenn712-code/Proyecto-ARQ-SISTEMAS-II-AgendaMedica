package servicios;

import dto.MedicamentoDTO;
import entidades.Medicamento;
import entidades.Paciente;
import entidades.TipoServicio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import repositorios.MedicamentoRepositorio;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<MedicamentoDTO> listarMedicamentosPorEstado(Integer pacCedula, String estado) {
        List<Object[]> results = medicamentoRepositorio.listarMedicamentos(pacCedula, estado);
        List<MedicamentoDTO> medicamentos = new ArrayList<>();

        for (Object[] row : results) {
            MedicamentoDTO dto = new MedicamentoDTO();
            dto.medId = (Integer) row[0];
            dto.medNombre = (String) row[1];
            dto.medDosis = (String) row[2];
            dto.medFrecuencia = (Integer) row[3];
            if (row[4] instanceof Timestamp ts) {
                dto.medFecha = ts.toLocalDateTime();
            } else if (row[4] instanceof LocalDateTime ldt) {
                dto.medFecha = ldt;
            } else {
                dto.medFecha = null;
            }
            dto.medDuracion = (Integer) row[5];
            dto.pacCedula = (Integer) row[6];
            dto.medEstado = (String) row[7];
            medicamentos.add(dto);
        }
        return medicamentos;
    }
}

