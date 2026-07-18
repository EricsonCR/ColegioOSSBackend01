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
public class ConsolidadoNotaItemResponse {

    private Long matriculaId;
    private String estudianteNombre;
    private String estudianteDocumento;
    private Long notaId;
    private BigDecimal valor;
    private String observacion;
    private boolean tieneNota;
}