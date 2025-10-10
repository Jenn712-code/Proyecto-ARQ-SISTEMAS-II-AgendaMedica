package repositorios;

import entidades.Cita;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class CitaRepositorio implements PanacheRepository<Cita>{
}

