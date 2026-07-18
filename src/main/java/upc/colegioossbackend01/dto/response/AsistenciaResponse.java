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
public class AsistenciaResponse {

    private Long id;
    private Long claseId;
    private Long matriculaId;
    private String estudianteNombre;
    private EstadoAsistencia estado;
    private String observacion;
    private boolean activo;
}