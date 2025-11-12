package repositorios;

import entidades.Cita;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CitaRepositorio implements PanacheRepository<Cita>{

    public List<Object[]> listarCitas(Integer pacCedula, String estado) {
        return getEntityManager().createQuery(
                        "SELECT c.citId, c.citNomMedico, c.citFecha, c.citHora, c.citDireccion, " +
                                "c.citEstado, c.paciente.pacCedula, c.especialidad.espId, e.espNombre " +
                                "FROM Cita c JOIN c.especialidad e " +
                                "WHERE c.paciente.pacCedula = :pacCedula AND c.citEstado = :estado", Object[].class)
                .setParameter("pacCedula", pacCedula)
                .setParameter("estado", estado)
                .getResultList();
    }
}

