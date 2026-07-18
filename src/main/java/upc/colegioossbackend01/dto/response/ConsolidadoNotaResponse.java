package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidadoNotaResponse {

    private Long cursoEvaluacionId;
    private String componenteNombre;
    private Integer porcentaje;
    private Integer bimestre;
    private String cursoNombre;
    private String aulaDescripcion;
    private List<ConsolidadoNotaItemResponse> estudiantes;
}