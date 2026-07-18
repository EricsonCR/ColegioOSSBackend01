package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidadoAsistenciaResponse {

    private Long claseId;
    private LocalDate fecha;
    private String tema;
    private String cursoNombre;
    private String aulaDescripcion;
    private List<ConsolidadoAsistenciaItemResponse> estudiantes;
}