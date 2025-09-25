package servicios;

import entidades.Paciente;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.Date;

@ApplicationScoped
public class DatosPrueba {

    @Transactional
    public void init(@Observes StartupEvent ev) {
        System.out.println(">>> Cargando datos iniciales...");

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
            System.out.println(">>> Paciente de prueba insertado");
        }
    }
}
