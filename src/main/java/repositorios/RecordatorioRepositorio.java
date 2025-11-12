package repositorios;

import entidades.Recordatorio;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecordatorioRepositorio implements PanacheRepositoryBase<Recordatorio, Integer> {
    public Recordatorio findByPacienteYTipo(Integer pacCedula, String tipoNombre) {
        return find("paciente.pacCedula = ?1 and tipoServicio.tipNombre = ?2", pacCedula, tipoNombre)
                .firstResult();
    }
}
