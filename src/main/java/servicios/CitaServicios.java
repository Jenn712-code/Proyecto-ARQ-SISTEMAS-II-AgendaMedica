package servicios;

import entidades.Cita;
import Repositorio.CitaRepositorio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CitaServicios {

    @Inject
    CitaRepositorio citaRepositorio;

    @Transactional
    public Cita crear(Cita cita) {
        citaRepositorio.persist(cita);
        return cita;
    }

    public List<Cita> listarCitas() {
        return citaRepositorio.listAll();
    }
}
