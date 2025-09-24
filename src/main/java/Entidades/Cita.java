package Entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="Citas")

public class Cita extends PanacheEntity{
    private Integer citId;
    private String citNomMedico;
    private Date citFecha;
    private Time citHora;
    private String citDireccion;
    private String citEstado;
    private boolean citRecordatorio;
}