package entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(name = "citas")
public class Cita extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "citId", length = 10, nullable = false, unique = true)
    private Integer citId;

    @Column(name = "citNomMedico", length = 100, nullable = false)
    private String citNomMedico;

    @Column(name = "citFecha", nullable = false)
    private LocalDate citFecha;

    @Column(name = "citHora", nullable = false)
    private LocalTime citHora;

    @Column(name = "citDireccion", length = 200, nullable = false)
    private String citDireccion;

    @Column(name = "citEstado", length = 50, nullable = false)
    private String citEstado;

    @Column(name = "citRecordatorio", nullable = false)
    private Boolean citRecordatorio;

    @ManyToOne
    @JoinColumn(name = "pacCedula", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "espId", nullable = false)
    private Especialidad especialidad;

    @ManyToOne
    @JoinColumn(name = "tipId", nullable = false)
    private TipoServicio tipoServicio ;
}