package servicios;

import entidades.Paciente;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.Date;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ApplicationScoped
@SuppressWarnings("java:S3252")
public class DatosPrueba {

    @Transactional
    public void init(@Observes StartupEvent ev) {
        LOGGER.info(">>> Cargando datos iniciales...");

        if (Paciente.find("pacCorreo", "jlopezv6@ucentral.edu.co").firstResult() == null) {
            Paciente paciente = new Paciente();
            paciente.setPacCedula(1002);
            paciente.setPacNombre("Jennifer López");
            paciente.setPacFecNacimiento(new Date());
            paciente.setPacEPS("Compensar");
            paciente.setPacCelular(3001234567L);
            paciente.setPacCorreo("jlopezv6@ucentral.edu.co");
            paciente.setPacContrasena(BcryptUtil.bcryptHash("123456")); // clave encriptada
            paciente.persist();
            LOGGER.info(">>> Paciente de prueba insertado");
        }
    }
}
