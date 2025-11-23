package servicios;

import dto.RecordatorioDTO;
import entidades.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Collections;
import lombok.AllArgsConstructor;
import repositorios.RecordatorioRepositorio;
import repositorios.TipoServicioRepositorio;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@AllArgsConstructor
@SuppressWarnings("java:S3252")
public class RecordatorioServicio {

    RecordatorioRepositorio recordatorioRepositorio;
    TipoServicioRepositorio tipoServicioRepositorio;
    NotificacionServicios notificacionServicios;
    private static final String QUERY = "tipoServicio.tipId = ?1 AND paciente.pacCedula = ?2";

    @Transactional
    public Recordatorio configurarRecordatorio(RecordatorioDTO dto, Integer cedulaPaciente) {

        // Verificar que el tipo de servicio exista
        TipoServicio tipoServicio = tipoServicioRepositorio.findById(dto.tipoServicio);
        if (tipoServicio == null) {
            throw new IllegalArgumentException("El tipo de servicio con ID " + dto.tipoServicio + " no existe");
        }

        Paciente paciente = Paciente.findById(cedulaPaciente);
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente con cédula " + cedulaPaciente + " no existe");
        }

        // Buscar si ya existe un recordatorio para este paciente y tipo de servicio
        Recordatorio existente = Recordatorio.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();

        if (existente != null) {
            // Actualizar recordatorio existente
            long anticipacionAnterior = existente.getRecAnticipacion();
            existente.setRecAnticipacion(dto.recAnticipacion);
            existente.setRecUnidadTiempo(dto.recUnidadTiempo);
            Recordatorio.getEntityManager().merge(existente);

            // Actualizar notificaciones solo si hay registros de Cita o Medicamento
            Cita cita = Cita.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();
            Medicamento medicamento = Medicamento.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();
            if (cita != null || medicamento != null) {
                RecordatorioDTO dtoActualizado = new RecordatorioDTO();
                dtoActualizado.recId = existente.getRecId();
                dtoActualizado.recAnticipacion = dto.recAnticipacion;
                dtoActualizado.tipoServicio = dto.tipoServicio;
                notificacionServicios.actualizarNotificacionesPorCambioDeRecordatorio(dtoActualizado, anticipacionAnterior);
            }

            return existente;
        } else {
            // Crear nuevo recordatorio global aunque no haya Cita o Medicamento aún
            Recordatorio nuevo = new Recordatorio();
            nuevo.setPaciente(paciente);
            nuevo.setTipoServicio(tipoServicio);
            nuevo.setRecAnticipacion(dto.recAnticipacion);
            nuevo.setRecUnidadTiempo(dto.recUnidadTiempo);
            nuevo.persist();

            return nuevo;
        }
    }

    @Transactional
    public Map<String, Object> obtenerRecordatorio(Long tipoServicioId, String cedulaPaciente) throws Exception {
        try {
            // Obtenemos directamente el recordatorio
            Recordatorio recordatorio = Recordatorio.find(QUERY, tipoServicioId, cedulaPaciente).firstResult();

            if (recordatorio == null) {
                // Sin recordatorio → entregar vacío (Frontend interpreta como No Configurado)
                return Collections.emptyMap();
            }

            // Convertir anticipación total a días, horas y minutos
            return getStringObjectMap(tipoServicioId, recordatorio);

        } catch (Exception e) {
            throw new Exception("Error al obtener recordatorio: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> getStringObjectMap(Long tipoServicioId, Recordatorio recordatorio) {
        long totalMinutos = recordatorio.getRecAnticipacion() != null ? recordatorio.getRecAnticipacion() : 0;
        Long dias = totalMinutos / (24 * 60);
        Long horas = (totalMinutos % (24 * 60)) / 60;
        Long minutos = totalMinutos % 60;

        //Crear respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("dias", dias);
        response.put("horas", horas);
        response.put("minutos", minutos);
        response.put("tipoServicioId", tipoServicioId);
        return response;
    }
}

