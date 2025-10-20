package servicios;

import entidades.Especialidad;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import repositorios.EspecialidadRepositorio;
import java.util.List;
@ApplicationScoped
@AllArgsConstructor
public class EspecialidadServicio {

    EspecialidadRepositorio especialidadRepositorio;
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepositorio.listAll();
    }
}
