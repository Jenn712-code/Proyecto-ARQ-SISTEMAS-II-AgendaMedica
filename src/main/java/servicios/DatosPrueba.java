package servicios;

import entidades.Especialidad;
import entidades.Paciente;
import entidades.TipoServicio;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.logging.Logger;

@ApplicationScoped
public class DatosPrueba {
    private static final Logger LOG = Logger.getLogger(String.valueOf(DatosPrueba.class));
    @Transactional
    public void init(@Observes StartupEvent ev) {
        LOG.info(">>> Cargando datos iniciales...");

        if (Paciente.find("pacCorreo", "ana@correo.com").firstResult() == null) {
            Paciente paciente = new Paciente();
            paciente.setPacCedula(1001);
            paciente.setPacNombre("Ana Pérez");
            paciente.setPacFecNacimiento(new Date());
            paciente.setPacEPS("Sura");
            paciente.setPacCelular(3001234567L);
            paciente.setPacCorreo("ana@correo.com");
            paciente.setPacContrasena(BcryptUtil.bcryptHash("123456")); // clave encriptada
            paciente.persist();
            LOG.info(">>> Paciente de prueba insertado");
        }

        if (Especialidad.find("nombre", "Medicina general").firstResult() == null) {
            Especialidad especialidad = new Especialidad();
            especialidad.setNombre("Medicina general");
            especialidad.persist();
            LOG.info(">>> Especialidad de prueba insertada");
        }

            TipoServicio tipoServicio = new TipoServicio();
            tipoServicio.setTipnombre("Cita");
            tipoServicio.persist();
            TipoServicio tipoServicio1 = new TipoServicio();
            tipoServicio1.setTipnombre("Medicamento");
            tipoServicio1.persist();
            LOG.info(">>> TipoServicio de prueba insertada");

    }
}
