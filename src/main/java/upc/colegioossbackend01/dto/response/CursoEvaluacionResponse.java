package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoEvaluacionResponse {

    private Long id;
    private Long aulaCursoId;
    private Integer bimestre;
    private EvaluacionResponse evaluacion;
    private String nombre;
    private Integer porcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;
}