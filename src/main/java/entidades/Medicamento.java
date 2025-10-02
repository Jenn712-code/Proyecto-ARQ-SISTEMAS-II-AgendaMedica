package entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table (name = "Medicamentos")
public class Medicamento extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medId", length = 50, nullable = false, unique = true)
    private Integer medId;

    @Column(name = "medNombre", length = 100, nullable = false)
    private String medNombre;

    @Column(name = "medDosis", length = 50, nullable = false)
    private String medDosis;

    @Column(name = "medFrecuencia", length = 50, nullable = false)
    private Integer medFrecuencia;

    @Column(name = "medDuracion", length = 50, nullable = false)
    private Integer medDuracion;

    @Column(name = "medEstado", length = 50, nullable = false)
    private String medEstado;

    @Column(name = "medRecordatorio", nullable = false)
    private Boolean medRecordatorio;

    @ManyToOne
    @JoinColumn(name = "pacCedula")
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "TipId")
    private TipoServicio tipoServicio;

    @Column(name = "Fecha", nullable = false)
    private LocalDate Fecha;

}




