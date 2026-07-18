package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AulaCursoRequest {

    @NotNull(message = "El aula es obligatoria")
    private Long aulaId;

    @NotNull(message = "El curso es obligatorio")
    private Long cursoId;

    private Integer horasSemana;
}