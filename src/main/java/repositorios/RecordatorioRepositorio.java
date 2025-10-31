package repositorios;

import entidades.Recordatorio;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecordatorioRepositorio implements PanacheRepository<Recordatorio> { }
