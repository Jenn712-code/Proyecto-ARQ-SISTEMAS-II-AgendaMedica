package servicios;

import entidades.Paciente;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@SuppressWarnings("java:S3252")
public class IniciarSesionServicio {

    public Paciente autenticacion(String correo, String contrasena){
        if (correo == null || contrasena == null) {
            return null;
        }

        Paciente paciente = Paciente.find("pacCorreo", correo).firstResult();

        if (paciente == null){
            return null;
        }
        if (!BcryptUtil.matches(contrasena, paciente.getPacContrasena())) {
            return null;
        }

        return paciente;

    }
}