package entidades;

import jakarta.persistence.*;

public class Recordatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId", length = 10, nullable = false, unique = true)
    private Integer RecId;

    @OneToOne
    @JoinColumn(name = "TipId")
    private TipoServicio tipoServicio;

    @Column(name = "RecAnticipacion", length = 10, nullable = false, unique = true)
    private Integer RecAnticipacion;

}
