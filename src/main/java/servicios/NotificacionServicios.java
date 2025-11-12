package servicios;

import dto.CitaDTO;
import dto.MedicamentoDTO;
import dto.NotificacionDTO;
import dto.RecordatorioDTO;
import entidades.Notificacion;
import entidades.Recordatorio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import repositorios.NotificacionRepositorio;
import repositorios.RecordatorioRepositorio;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;


@AllArgsConstructor
@ApplicationScoped
public class NotificacionServicios {

    RecordatorioRepositorio recordatorioRepositorio;
    NotificacionRepositorio notificacionRepositorio;
    private static final Logger logger = Logger.getLogger(NotificacionServicios.class.getName());

    //Genera una notificación para una cita médica si el campo citRecordatorio es true
    @Transactional
    public void generarNotificacionParaCita(CitaDTO cita) {
        if (Boolean.FALSE.equals(cita.citRecordatorio)) return;

        Recordatorio recordatorio = recordatorioRepositorio.findByPacienteYTipo(cita.pacCedula, "Cita");
        if (recordatorio == null) return;

        // Combinar fecha + hora de la cita
        LocalDateTime fechaHoraCita = LocalDateTime.of(cita.citFecha, cita.citHora);

        // Calcular fecha programada (restando minutos de anticipación)
        LocalDateTime fechaNotificacion = fechaHoraCita.minusMinutes(recordatorio.getRecAnticipacion());

        // Crear la notificación
        Notificacion notificacion = new Notificacion();
        notificacion.setNotFecha(fechaNotificacion);
        notificacion.setNotEstado(false);
        notificacion.setRecordatorio(recordatorio);
        notificacion.setIdReferencia(cita.citId);
        notificacion.setTipoReferencia("Cita");

        notificacionRepositorio.persist(notificacion);
    }

    //Genera notificaciones para un medicamento según su duración (en días).

    @Transactional
    public void generarNotificacionesParaMedicamento(MedicamentoDTO medicamento) {
        if (Boolean.FALSE.equals(medicamento.medRecordatorio)) return;

        Recordatorio recordatorio = recordatorioRepositorio.findByPacienteYTipo(medicamento.pacCedula, "Medicamento");
        if (recordatorio == null) return;

        int dias = medicamento.medDuracion;
        int frecuenciaHoras = medicamento.medFrecuencia; // Ejemplo: cada 12 horas
        LocalDateTime fechaInicio = medicamento.medFecha;

        List<Notificacion> notificaciones = new ArrayList<>();

        // Recorremos cada día del tratamiento
        for (int d = 0; d < dias; d++) {
            // Por cada día, generamos las notificaciones según la frecuencia
            for (int h = 0; h < 24; h += frecuenciaHoras) {
                // Calcular la hora de cada dosis dentro del día
                LocalDateTime fechaDosis = fechaInicio.plusDays(d).plusHours(h);

                // Calcular la hora de notificación (restando la anticipación)
                LocalDateTime fechaNotificacion = fechaDosis.minusMinutes(recordatorio.getRecAnticipacion());

                // Crear el objeto Notificacion
                Notificacion n = new Notificacion();
                n.setNotFecha(fechaNotificacion);
                n.setNotEstado(false);
                n.setRecordatorio(recordatorio);
                n.setIdReferencia(medicamento.medId);
                n.setTipoReferencia("Medicamento");

                notificaciones.add(n);
            }
        }
        // Guardar todas las notificaciones
        notificaciones.forEach(notificacionRepositorio::persist);
    }

    @Transactional
    public void actualizarNotificacionesPorCambioDeRecordatorio(RecordatorioDTO recordatorioActualizado, long anticipacionAnterior) {
        List<Notificacion> futuras = notificacionRepositorio.listarPorRecordatorioYEstado(recordatorioActualizado.recId, false);

        if (futuras.isEmpty()) return;

        long nuevaAnticipacion = recordatorioActualizado.recAnticipacion;
        LocalDateTime ahora = LocalDateTime.now();

        for (Notificacion n : futuras) {
            // Recuperamos la fecha original del evento
            LocalDateTime fechaEvento = n.getNotFecha().plusMinutes(anticipacionAnterior);
            // Recalculamos con la nueva anticipación
            LocalDateTime nuevaFecha = fechaEvento.minusMinutes(nuevaAnticipacion);

            // Si la nueva fecha aún es futura → actualizarla
            if (nuevaFecha.isAfter(ahora)) {
                n.setNotFecha(nuevaFecha);
            }
        }
    }

    public List<NotificacionDTO> obtenerNotificacionesPendientesPorCedula(Integer cedula) {
        List<Object[]> results = notificacionRepositorio.obtenerNotificacionesPorCedula(cedula);
        List<NotificacionDTO> notificaciones = new ArrayList<>();

        for (Object[] row : results) {
            NotificacionDTO dto = new NotificacionDTO();
            dto.notId = (Integer) row[0];
            if (row[1] instanceof Timestamp ts) {
                dto.notFecha = ts.toLocalDateTime();
            } else if (row[1] instanceof LocalDateTime ldt) {
                dto.notFecha = ldt;
            } else {
                dto.notFecha = null;
            }
            dto.notEstado = row[2] != null ? (Boolean) row[2] : false;
            dto.tipoReferencia = (String) row[3];
            dto.idReferencia = (Integer) row[4];

            if ("Cita".equals(dto.tipoReferencia)) {
                dto.citNomMedico = (String) row[5];
                if (row[6] instanceof java.sql.Date sqlDate) {
                    dto.citFecha = sqlDate.toLocalDate();
                } else if (row[6] instanceof java.time.LocalDate localDate) {
                    dto.citFecha = localDate;
                } else {
                    dto.citFecha = null;
                }

                if (row[7] instanceof java.sql.Time sqlTime) {
                    dto.citHora = sqlTime.toLocalTime();
                } else if (row[7] instanceof java.time.LocalTime localTime) {
                    dto.citHora = localTime;
                } else {
                    dto.citHora = null;
                }
                dto.citDireccion = (String) row[8];
                dto.citEspecialidad = (String) row[9];
            } else if ("Medicamento".equals(dto.tipoReferencia)) {
                dto.medNombre = (String) row[10];
                dto.medDosis = (String) row[11];
                dto.medFrecuencia = (Integer) row[12];
                if (row[13] instanceof Timestamp ts) {
                    dto.medFecha = ts.toLocalDateTime();
                } else if (row[13] instanceof LocalDateTime ldt) {
                    dto.medFecha = ldt;
                } else {
                    dto.medFecha = null;
                }
            }

            // Solo notificaciones pendientes
            if (!dto.notEstado) {
                notificaciones.add(dto);
            }
        }
        return notificaciones;
    }
}