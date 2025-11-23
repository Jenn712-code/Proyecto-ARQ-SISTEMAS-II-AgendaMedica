package servicios;

import dto.PacienteDTO;
import entidades.Paciente;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import repositorios.PacienteRepositorio;

@ApplicationScoped
@AllArgsConstructor
public class PacienteServicios {

    PacienteRepositorio pacienteRepositorio;

    @Transactional
    public Paciente crearPaciente(PacienteDTO dto) {

        // Validación de cédula única
        if (pacienteRepositorio.existsByCedula(dto.pacCedula)) {
            throw new WebApplicationException(
                    "La cédula ya está registrada",
                    Response.Status.CONFLICT
            );
        }

        // Validación de correo único
        if (pacienteRepositorio.existsByCorreo(dto.pacCorreo)) {
            throw new WebApplicationException(
                    "El correo ya está registrado",
                    Response.Status.CONFLICT
            );
        }

        // Construcción del entity
        Paciente paciente = new Paciente();
        paciente.setPacCedula(dto.pacCedula);
        paciente.setPacNombre(dto.pacNombre);
        paciente.setPacFecNacimiento(dto.pacFecNacimiento);
        paciente.setPacEPS(dto.pacEPS);
        paciente.setPacCelular(dto.pacCelular);
        paciente.setPacCorreo(dto.pacCorreo);
        paciente.setPacContrasena(dto.pacContrasena);

        paciente.setPacContrasena(
                PasswordEncoder.hash(dto.pacContrasena)
        );

        paciente.persist();
        return paciente;
    }

    public class PasswordEncoder {

        public static String hash(String password) {
            return BcryptUtil.bcryptHash(password);
        }
    }
}
