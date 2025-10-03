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

@ApplicationScoped
public class DatosPrueba {
    private static final Logger LOG = Logger.getLogger(String.valueOf(DatosPrueba.class));
    @Transactional
    public void init(@Observes StartupEvent ev) {
        LOG.info(">>> Cargando datos iniciales...");

        Paciente paciente = Paciente.find("pacCorreo", "ana@correo.com").firstResult();
        if (paciente == null){
            paciente = new Paciente();
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
            cita.setCitEstado("pendiente");
            cita.setCitRecordatorio(true);
            cita.setPaciente(paciente);
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
            medicamento.setMedEstado("pendiente");
            medicamento.setMedRecordatorio(true);
            medicamento.setMedFecha(LocalDateTime.now()); // fecha-hora actual
            medicamento.setPaciente(paciente);
            TipoServicio tipo = TipoServicio.findById(2);
            medicamento.setTipoServicio(tipo);

            medicamento.persist();
            LOG.info(">>> Medicamento de prueba insertado");
        }

    }


    private void insertarEspecialidadSiNoExiste(String nombre) {
        long count = Especialidad.count("nombre", nombre);
        if (count == 0) {
            Especialidad especialidad = new Especialidad();
            especialidad.setNombre(nombre);
            especialidad.persist();
            LOG.info(">>> Especialidad '" + nombre + "' insertada");
        }
    }

    private void insertarSiNoExiste(String nombre) {
        long count = TipoServicio.count("tipnombre", nombre);
        if (count == 0) {
            TipoServicio tipoServicio = new TipoServicio();
            tipoServicio.setTipnombre(nombre);
            tipoServicio.persist();
            LOG.info(">>> TipoServicio '" + nombre + "' insertado");
        }
    }
}
