package upc.colegioossbackend01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "nota")
public class Nota extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_evaluacion_id", nullable = false)
    private CursoEvaluacion cursoEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(length = 255)
    private String observacion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}