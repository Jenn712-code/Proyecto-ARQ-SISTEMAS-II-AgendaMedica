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
@Table(name = "Especialidades")
public class Especialidad extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "espId", length = 10, nullable = false, unique = true)
    private Integer espId;
    @Column(name = "espNombre", length = 50, nullable = false)
    private String espNombre;

}
