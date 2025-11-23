package repositorios;

import entidades.Medicamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MedicamentoRepositorio implements PanacheRepository<Medicamento> {

    public List<Object[]> listarMedicamentos(Integer pacCedula, String estado) {
        return getEntityManager().createQuery(
                        "SELECT m.medId, m.medNombre, m.medDosis, m.medFrecuencia, m.medFecha, " +
                                "m.medDuracion, m.paciente.pacCedula, m.medEstado " +
                                "FROM Medicamento m " +
                                "WHERE m.paciente.pacCedula = :pacCedula AND m.medEstado = :estado", Object[].class)
                .setParameter("pacCedula", pacCedula)
                .setParameter("estado", estado)
                .getResultList();
    }
}
