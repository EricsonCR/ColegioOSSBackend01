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
public class DocenteCursoResponse {

    private Long id;
    private AulaCursoResponse aulaCurso;
    private Long usuarioId;
    private String docenteNombre;
    private boolean activo;
}