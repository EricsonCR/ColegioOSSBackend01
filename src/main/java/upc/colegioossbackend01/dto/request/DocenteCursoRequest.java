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
public class DocenteCursoRequest {

    @NotNull(message = "El curso-aula es obligatorio")
    private Long aulaCursoId;

    @NotNull(message = "El docente es obligatorio")
    private Long usuarioId;
}