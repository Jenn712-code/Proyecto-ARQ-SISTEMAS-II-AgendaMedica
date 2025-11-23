package dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;

import java.util.Date;

public class PacienteDTO {

    public Integer pacCedula;
    public String pacNombre;
    public Date pacFecNacimiento;
    public String pacEPS;
    public Long pacCelular;
    public String pacCorreo;
    public String pacContrasena;
}
