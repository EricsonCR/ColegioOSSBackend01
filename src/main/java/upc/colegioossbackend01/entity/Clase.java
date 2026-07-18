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
@Table(name = "clase")
public class Clase extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_curso_id", nullable = false)
    private AulaCurso aulaCurso;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 150)
    private String tema;

    @Column(length = 255)
    private String observacion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
}