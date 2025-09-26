package servicios;

import dto.CitaDTO;
import entidades.Cita;
import entidades.Especialidad;
import entidades.Paciente;
import repositorio.CitaRepositorio;

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
        if (paciente == null) return null;

        Especialidad especialidad = Especialidad.findById(dto.espId);
        if (especialidad == null) return null;

        Cita cita = new Cita();
        cita.setCitNomMedico(dto.citNomMedico);
        cita.setCitFecha(dto.citFecha);
        cita.setCitHora(dto.citHora);
        cita.setCitDireccion(dto.citDireccion);
        cita.setCitEstado(dto.citEstado);
        cita.setCitRecordatorio(dto.citRecordatorio);
        cita.setPaciente(paciente);
        cita.setEspecialidad(especialidad);

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
