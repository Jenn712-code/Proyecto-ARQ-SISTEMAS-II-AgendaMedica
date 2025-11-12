package entidades;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table (name = "notificaciones")
public class Notificacion extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notId", length = 50, nullable = false, unique = true)
    private Integer notId;

    @Column(name = "notFecha", nullable = false)
    private LocalDateTime notFecha;

    @Column(name = "notEstado", nullable = false)
    private Boolean notEstado;

    @ManyToOne
    @JoinColumn(name = "recId", nullable = false)
    private Recordatorio recordatorio;

    @Column(name = "idReferencia")
    private Integer idReferencia;

    @Column(name = "tipoReferencia")
    private String tipoReferencia;
}
