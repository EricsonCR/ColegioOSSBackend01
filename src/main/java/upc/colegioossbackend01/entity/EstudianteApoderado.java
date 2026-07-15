package upc.colegioossbackend01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "estudiante_apoderado",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "apoderado_id"}))
public class EstudianteApoderado extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apoderado_id", nullable = false)
    private Apoderado apoderado;

    @Column(length = 50)
    private String parentesco;

    @Builder.Default
    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}