package repositorios;

import entidades.Especialidad;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EspecialidadRepositorio implements PanacheRepository<Especialidad> {
}
