package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CursoEvaluacionRequest {

    @NotNull(message = "El curso-aula es obligatorio")
    private Long aulaCursoId;

    @NotNull(message = "El bimestre es obligatorio")
    @Min(value = 1, message = "El bimestre debe ser entre 1 y 4")
    @Max(value = 4, message = "El bimestre debe ser entre 1 y 4")
    private Integer bimestre;

    @NotNull(message = "El tipo de evaluación es obligatorio")
    private Long evaluacionId;

    private String nombre;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El porcentaje debe ser mayor a 0")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private Integer porcentaje;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}