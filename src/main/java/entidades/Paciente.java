package Entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table (name = "Pacientes")
public class Paciente extends PanacheEntity {

    private Integer pacId;
    private String pacNombre;
    private Date pacFecNacimiento;
    private String pacEPS;
    private Integer pacCelular;
    private String pacCorreo;
    private String pacContraseña;
}

//@ManyToOne(targetEntity = Paciente.class)
//@JoinColumn(name = "pacId")
//@ToString.Exclude
//private Paciente paciente;
