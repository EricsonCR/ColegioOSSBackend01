package upc.colegioossbackend01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import upc.colegioossbackend01.enums.Nivel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "aula")
public class Aula extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer periodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Nivel nivel;

    @Column(nullable = false)
    private Integer grado;

    @Column(nullable = false, length = 5)
    private String seccion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}