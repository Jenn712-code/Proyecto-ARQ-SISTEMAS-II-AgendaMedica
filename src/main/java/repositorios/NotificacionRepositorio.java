package repositorios;

import entidades.Notificacion;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class NotificacionRepositorio implements PanacheRepository<Notificacion> {

    @Inject
    EntityManager entityManager;

    public List<Notificacion> listarPorRecordatorioYEstado(Integer recId, boolean estado) {
        LocalDateTime ahora = LocalDateTime.now();
        return find("recordatorio.recId = ?1 and notEstado = ?2 and notFecha > ?3",
                recId, estado, ahora).list();
    }

    public List<Object[]> obtenerNotificacionesPorCedula(Integer cedula) {
        String sql = """
        SELECT\s
                n.notid,
                n.notfecha,
                n.notestado,
                ts.tipnombre AS tiporeferencia,
                n.idreferencia,
                c.citnommedico,
                c.citfecha,
                c.cithora,
                c.citdireccion,
                e.espnombre AS citespecialidad,
                m.mednombre,
                m.meddosis,
                m.medfrecuencia,
                m.medfecha
            FROM notificaciones n
            JOIN recordatorios r ON n.recid = r.recid
            JOIN pacientes p ON r.paccedula = p.paccedula
            JOIN tiposervicio ts ON n.tiporeferencia = ts.tipnombre
            LEFT JOIN citas c ON n.idreferencia = c.citid AND ts.tipnombre = 'Cita'
            LEFT JOIN especialidades e ON c.espid = e.espid
            LEFT JOIN medicamentos m ON n.idreferencia = m.medid AND ts.tipnombre = 'Medicamento'
            WHERE p.paccedula = :pacCedula
            AND n.notestado = false
            ORDER BY n.notfecha DESC
    """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager
                .createNativeQuery(sql)
                .setParameter("pacCedula", cedula)
                .getResultList();

        return results;
    }
}
