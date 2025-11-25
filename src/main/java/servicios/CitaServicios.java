package servicios;

import dto.CitaDTO;
import entidades.Cita;
import entidades.Especialidad;
import entidades.Paciente;
import entidades.TipoServicio;
import lombok.AllArgsConstructor;
import repositorios.CitaRepositorio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@AllArgsConstructor
@SuppressWarnings("java:S3252")
public class CitaServicios {

    CitaRepositorio citaRepositorio;
    NotificacionServicios notificacionServicios;

    @Transactional
    public Cita crearCita(CitaDTO dto) {
        Paciente paciente = Paciente.findById(dto.pacCedula);
        if (paciente == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-2);
            return dummy;
        }

        Especialidad especialidad = Especialidad.findById(dto.espId);
        if (especialidad == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-3);
            return dummy;
        }

        Cita citaExistente = citaRepositorio.find("citFecha = ?1 and citHora = ?2", dto.citFecha, dto.citHora)
                .firstResult();
        if (citaExistente != null) {
            Cita dummy = new Cita();
            dummy.setCitId(-1);
            return dummy;
        }

        Cita cita = new Cita();
        cita.setCitNomMedico(dto.citNomMedico);
        cita.setCitFecha(dto.citFecha);
        cita.setCitHora(dto.citHora);
        cita.setCitDireccion(dto.citDireccion);
        cita.setCitEstado(dto.citEstado);
        if (dto.citEstado == null || dto.citEstado.isBlank()) {
            cita.setCitEstado("Pendiente");
        } else {
            cita.setCitEstado(dto.citEstado);
        }
        cita.setCitRecordatorio(dto.citRecordatorio);
        cita.setPaciente(paciente);
        cita.setEspecialidad(especialidad);
        TipoServicio tipo = TipoServicio.find("tipNombre", "Cita").firstResult();
        cita.setTipoServicio(tipo);

        citaRepositorio.persist(cita);
        citaRepositorio.flush();

        // asignar el ID generado al DTO antes de llamar a notificacionServicios
        dto.citId = cita.getCitId();
        notificacionServicios.generarNotificacionParaCita(dto);
        return cita;
    }
    public List<CitaDTO> listarCitasPorEstado(Integer pacCedula, String estado) {
        List<Object[]> results = citaRepositorio.listarCitas(pacCedula, estado);
        List<CitaDTO> citas = new ArrayList<>();

        for (Object[] row : results) {
            CitaDTO dto = new CitaDTO();
            dto.citId = (Integer) row[0];
            dto.citNomMedico = (String) row[1];

            // citFecha
            if (row[2] instanceof java.sql.Date sqlDate) {
                dto.citFecha = sqlDate.toLocalDate();
            } else if (row[2] instanceof java.time.LocalDate localDate) {
                dto.citFecha = localDate;
            } else {
                dto.citFecha = null;
            }

            // citHora
            if (row[3] instanceof java.sql.Time sqlTime) {
                dto.citHora = sqlTime.toLocalTime();
            } else if (row[3] instanceof java.time.LocalTime localTime) {
                dto.citHora = localTime;
            } else {
                dto.citHora = null;
            }

            dto.citDireccion = (String) row[4];
            dto.citEstado = (String) row[5];
            dto.citRecordatorio = (Boolean) row[6];
            dto.pacCedula = (Integer) row[7];
            dto.espId = (Integer) row[8];
            dto.espNombre = (String) row[9];

            citas.add(dto);
        }
        return citas;
    }

    @Transactional
    public Cita actualizarCita(CitaDTO dto) {

        Cita cita = Cita.findById(dto.citId);
        if (cita == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-4); // No existe la cita
            return dummy;
        }

        // Validar paciente
        Paciente paciente = Paciente.findById(dto.pacCedula);
        if (paciente == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-2); // Paciente no existe
            return dummy;
        }

        // Validar especialidad
        Especialidad especialidad = Especialidad.findById(dto.espId);
        if (especialidad == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-3); // Especialidad no existe
            return dummy;
        }

        // Validar si existe otra cita en esa fecha/hora
        Cita citaExistente = citaRepositorio.find(
                "citFecha = ?1 and citHora = ?2 and citId <> ?3",
                dto.citFecha, dto.citHora, dto.citId
        ).firstResult();

        if (citaExistente != null) {
            Cita dummy = new Cita();
            dummy.setCitId(-1); // Duplicada
            return dummy;
        }

        // === Actualizar campos ===
        cita.setCitNomMedico(dto.citNomMedico);
        cita.setCitFecha(dto.citFecha);
        cita.setCitHora(dto.citHora);
        cita.setCitDireccion(dto.citDireccion);

        if (dto.citEstado == null || dto.citEstado.isBlank()) {
            cita.setCitEstado("Pendiente");
        } else {
            cita.setCitEstado(dto.citEstado);
        }

        cita.setCitRecordatorio(dto.citRecordatorio);
        cita.setPaciente(paciente);
        cita.setEspecialidad(especialidad);

        TipoServicio tipo = TipoServicio.find("tipNombre", "Cita").firstResult();
        cita.setTipoServicio(tipo);

        citaRepositorio.persist(cita);
        citaRepositorio.flush();

        // Notificación
        notificacionServicios.generarNotificacionParaCita(dto);

        return cita;
    }

    @Transactional
    public Cita eliminarCita(Integer citId) {

        Cita cita = Cita.findById(citId);

        if (cita == null) {
            Cita dummy = new Cita();
            dummy.setCitId(-4); // No existe cita
            return dummy;
        }

        citaRepositorio.delete(cita);
        citaRepositorio.flush();

        return cita;
    }
}