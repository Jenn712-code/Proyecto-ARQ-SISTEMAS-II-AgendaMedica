package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NotificacionDTO {
    public Integer notId;
    public LocalDateTime notFecha;
    public Boolean notEstado;
    public String tipoReferencia; // "Cita" o "Medicamento"
    public Integer idReferencia;

    // Campos de Cita
    public String citNomMedico;
    public LocalDate citFecha;
    public LocalTime citHora;
    public String citDireccion;
    public String citEspecialidad;

    // Campos de Medicamento
    public String medNombre;
    public String medDosis;
    public Integer medFrecuencia;
    public LocalDateTime medFecha;
}
