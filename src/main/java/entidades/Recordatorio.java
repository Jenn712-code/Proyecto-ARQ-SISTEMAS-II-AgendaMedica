package entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table (name = "recordatorios")
public class Recordatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recId", length = 10, nullable = false, unique = true)
    private Integer recId;

    @OneToOne
    @JoinColumn(name = "tipId")
    private TipoServicio tipoServicio;

    @Column(name = "recAnticipacion")
    private LocalDateTime RecAnticipacion;

    @Column(name = "recUnidadTiempo", length = 10, nullable = false)
    private String recUnidadTiempo;
}
