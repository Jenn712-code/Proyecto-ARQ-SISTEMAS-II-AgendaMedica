package servicios;

import entidades.*;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.logging.Logger;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ApplicationScoped
@SuppressWarnings("java:S3252")
public class DatosPrueba {
    private static final Logger LOG = Logger.getLogger(String.valueOf(DatosPrueba.class));
    @Transactional
    public void init(@Observes StartupEvent ev) {
        LOGGER.info(">>> Cargando datos iniciales...");

        Paciente paciente = Paciente.find("pacCorreo", "jlopezv6@ucentral.edu.co").firstResult();
        if (paciente == null) {
            paciente = new Paciente();
            paciente.setPacCedula(1002);
            paciente.setPacNombre("Jennifer López");
            paciente.setPacFecNacimiento(new Date());
            paciente.setPacEPS("Compensar");
            paciente.setPacCelular(3001234567L);
            paciente.setPacCorreo("jlopezv6@ucentral.edu.co");
            paciente.setPacContrasena(BcryptUtil.bcryptHash("123456")); // clave encriptada
            paciente.persist();
            LOG.info(">>> Paciente Jennifer López insertado");
        }

        Paciente paciente1 = Paciente.find("pacCorreo", "ana@correo.com").firstResult();
        if (paciente1 == null) {
            paciente1 = new Paciente();
            paciente1.setPacCedula(1001);
            paciente1.setPacNombre("Ana Pérez");
            paciente1.setPacFecNacimiento(new Date());
            paciente1.setPacEPS("Sura");
            paciente1.setPacCelular(3007654321L);
            paciente1.setPacCorreo("ana@correo.com");
            paciente1.setPacContrasena(BcryptUtil.bcryptHash("123456"));
            paciente1.persist();
            LOG.info(">>> Paciente Ana Pérez insertado");
        }

        // Especialidades
        insertarEspecialidadSiNoExiste("Medicina general");

        // Tipos de servicio
        insertarSiNoExiste("Cita");
        insertarSiNoExiste("Medicamento");

        // Cita
        if (Cita.find("citNomMedico", "Dr. Pérez").firstResult() == null) {
            Cita cita = new Cita();
            cita.setCitNomMedico("Dr. Pérez");
            cita.setCitFecha(LocalDate.of(2025, 10, 1));   // yyyy-MM-dd
            cita.setCitHora(LocalTime.of(14, 30, 0));      // HH:mm:ss
            cita.setCitDireccion("Calle 123 #45-67");
            cita.setCitEstado("Pendiente");
            cita.setCitRecordatorio(true);
            cita.setPaciente(paciente1);
            Especialidad esp = Especialidad.findById(1);
            cita.setEspecialidad(esp);
            TipoServicio tipo = TipoServicio.findById(1);
            cita.setTipoServicio(tipo);

            cita.persist();
            LOG.info(">>> Cita de prueba insertada");
        }

        // Medicamento
        if (Medicamento.find("medNombre", "Ibuprofeno").firstResult() == null) {
            Medicamento medicamento = new Medicamento();
            medicamento.setMedNombre("Ibuprofeno");
            medicamento.setMedDosis("1 pastilla");
            medicamento.setMedFrecuencia(8);
            medicamento.setMedDuracion(5);
            medicamento.setMedEstado("Pendiente");
            medicamento.setMedRecordatorio(true);
            medicamento.setMedFecha(LocalDateTime.now()); // fecha-hora actual
            medicamento.setPaciente(paciente1);
            TipoServicio tipo = TipoServicio.findById(2);
            medicamento.setTipoServicio(tipo);

            medicamento.persist();
            LOG.info(">>> Medicamento de prueba insertado");
        }

        LOGGER.info(">>> Cargando datos iniciales...");
    }

    private void insertarEspecialidadSiNoExiste(String nombre) {
        long count = Especialidad.count("espNombre", nombre);
        if (count == 0) {
            Especialidad especialidad = new Especialidad();
            especialidad.setEspNombre(nombre);
            especialidad.persist();
            LOG.info(">>> Especialidad '{}' insertada");
        }
    }

    private void insertarSiNoExiste(String nombre) {
        long count = TipoServicio.count("tipnombre", nombre);
        if (count == 0) {
            TipoServicio tipoServicio = new TipoServicio();
            tipoServicio.setTipnombre(nombre);
            tipoServicio.persist();
            LOG.info(">>> TipoServicio '{}' insertado");
        }
    }
}