package upc.colegioossbackend01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "curso_evaluacion")
public class CursoEvaluacion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_curso_id", nullable = false)
    private AulaCurso aulaCurso;

    @Column(nullable = false)
    private Integer bimestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @Column(length = 50)
    private String nombre;

    @Column(nullable = false)
    private Integer porcentaje;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}