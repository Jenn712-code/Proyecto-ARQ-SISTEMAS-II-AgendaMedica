package servicios;

import dto.CitaDTO;
import entidades.Cita;
import entidades.Especialidad;
import entidades.Paciente;
import entidades.TipoServicio;
import repositorios.CitaRepositorio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CitaServicios {

    @Inject
    CitaRepositorio citaRepositorio;

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
            cita.setCitEstado("pendiente");
        } else {
            cita.setCitEstado(dto.citEstado);
        }
        cita.setCitRecordatorio(dto.citRecordatorio);
        cita.setPaciente(paciente);
        cita.setEspecialidad(especialidad);
        TipoServicio tipo = TipoServicio.find("tipnombre", "Cita").firstResult();
        cita.setTipoServicio(tipo);

        citaRepositorio.persist(cita);
        return cita;
    }
    public List<Cita> listarCitasPorPaciente(Integer pacCedula) {
        return citaRepositorio.list("paciente.pacCedula", pacCedula);
    }
    public List<Cita> listarCitas() {
        return citaRepositorio.listAll();
    }
}
