package entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table (name = "Pacientes")
public class Paciente extends PanacheEntityBase {

    @Id
    @Column(name = "pacCedula", length = 10, nullable = false, unique = true)
    private Integer pacCedula;
    @Column(name = "pacNombre", length = 100, nullable = false)
    private String pacNombre;
    @Temporal(TemporalType.DATE)
    @Column(name = "pacFecNacimiento", nullable = false)
    private Date pacFecNacimiento;
    @Column(name = "pacEPS", length = 50, nullable = false)
    private String pacEPS;
    @Column(name = "pacCelular", length = 10, nullable = false)
    private Long pacCelular;
    @Column(name = "pacCorreo", length = 150, nullable = false, unique = true)
    private String pacCorreo;
    @JsonIgnore
    @Column(name = "pacContrasena", length = 255, nullable = false)
    private String pacContrasena;
}