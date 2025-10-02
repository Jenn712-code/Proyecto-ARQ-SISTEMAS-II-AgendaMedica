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
@Table(name = "TipoServicio")

public class TipoServicio extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipId", length = 10, nullable = false, unique = true)
    private Integer TipId;

    @Column(name = "Tipnombre", length = 200, nullable = false)
    private String Tipnombre;
}
