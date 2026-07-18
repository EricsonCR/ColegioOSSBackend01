package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaResponse {

    private Long id;
    private Long cursoEvaluacionId;
    private String componenteNombre;
    private Integer porcentaje;
    private Long matriculaId;
    private String estudianteNombre;
    private BigDecimal valor;
    private String observacion;
    private boolean activo;
}