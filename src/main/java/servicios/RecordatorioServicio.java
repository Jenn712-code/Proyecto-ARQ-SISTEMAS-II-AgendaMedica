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
    private static final String QUERY = "tipoServicio.tipId = ?1 AND paciente.pacCedula = ?2";

    @Transactional
    public Recordatorio configurarRecordatorio(RecordatorioDTO dto, Integer cedulaPaciente) {

        //Verificar que el tipo de servicio exista
        TipoServicio tipoServicio = tipoServicioRepositorio.findById(dto.tipoServicio);
        if (tipoServicio == null) {
            throw new IllegalArgumentException("El tipo de servicio con ID " + dto.tipoServicio + " no existe");
        }

        //Verificar si el tipo de servicio pertenece a una cita o medicamento del paciente
        boolean perteneceAPaciente = false;

        // Buscar cita
        Cita cita = Cita.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();

        // Buscar medicamento
        Medicamento medicamento = Medicamento.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();

        if (cita != null || medicamento != null) {
            perteneceAPaciente = true;
        }

        if (!perteneceAPaciente) {
            throw new IllegalArgumentException("No hay registros con el tipo de servicio asociado");
        }

        // Buscar si ya existe un recordatorio global para este paciente y tipo de servicio
        Recordatorio existente = Recordatorio.find(QUERY, dto.tipoServicio, cedulaPaciente).firstResult();

        if (existente != null) {
            //Si ya existe → actualizar
            existente.setRecAnticipacion(dto.recAnticipacion);
            existente.setRecUnidadTiempo(dto.recUnidadTiempo);
            Recordatorio.getEntityManager().merge(existente);
            return existente;
        } else {
            //Si no existe → crear uno nuevo
            Paciente paciente = Paciente.findById(cedulaPaciente);
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
            Cita cita = Cita.find(QUERY, tipoServicioId, cedulaPaciente).firstResult();
            Medicamento medicamento = Medicamento.find(QUERY, tipoServicioId, cedulaPaciente).firstResult();

            if (cita == null && medicamento == null) {
                throw new IllegalArgumentException("El tipo de servicio no pertenece al paciente con cédula " + cedulaPaciente);
            }

            // Buscar el recordatorio asociado a ese tipo de servicio y paciente
            Recordatorio recordatorio = Recordatorio.find(QUERY, tipoServicioId, cedulaPaciente).firstResult();

            if (recordatorio == null) {
                // No hay recordatorio configurado para este paciente
                return Collections.emptyMap();
            }

            // Convertir la anticipación total a días, horas y minutos
            return getStringObjectMap(tipoServicioId, recordatorio);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            // Errores inesperados
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

