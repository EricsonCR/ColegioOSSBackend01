package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upc.colegioossbackend01.enums.Nivel;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AulaResponse {

    private Long id;
    private Integer periodo;
    private Nivel nivel;
    private Integer grado;
    private String seccion;
    private boolean activo;
}