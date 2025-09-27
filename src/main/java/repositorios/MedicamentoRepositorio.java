package repositorios;

import entidades.Medicamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MedicamentoRepositorio implements PanacheRepository<Medicamento> {
}
