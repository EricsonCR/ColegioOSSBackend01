package upc.colegioossbackend01.dto.response;

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
public class AulaCursoResponse {

    private Long id;
    private AulaResponse aula;
    private CursoResponse curso;
    private Integer horasSemana;
    private boolean activo;
}