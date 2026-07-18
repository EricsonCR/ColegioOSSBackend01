package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upc.colegioossbackend01.enums.EstadoAsistencia;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidadoAsistenciaItemResponse {

    private Long matriculaId;
    private String estudianteNombre;
    private String estudianteDocumento;
    private Long asistenciaId;
    private EstadoAsistencia estado;
    private String observacion;
    private boolean registrado;
}