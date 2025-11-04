package entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table (name = "recordatorios")
public class Recordatorio extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recId", length = 10, nullable = false, unique = true)
    private Integer recId;

    @ManyToOne
    @JoinColumn(name = "tipId")
    private TipoServicio tipoServicio;

    @Column(name = "recAnticipacion")
    private Long recAnticipacion;

    @Column(name = "recUnidadTiempo", length = 10, nullable = false)
    private String recUnidadTiempo;

    @ManyToOne
    @JoinColumn(name = "pacCedula")
    private Paciente paciente;
}
