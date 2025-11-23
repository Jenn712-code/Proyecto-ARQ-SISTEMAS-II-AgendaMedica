package repositorios;

import entidades.Paciente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PacienteRepositorio implements PanacheRepository<Paciente> {

    public boolean existsByCorreo(String correo) {
        return find("pacCorreo", correo).firstResult() != null;
    }

    public boolean existsByCedula(Integer cedula) {
        return find("pacCedula", cedula).firstResult() != null;
    }
}
