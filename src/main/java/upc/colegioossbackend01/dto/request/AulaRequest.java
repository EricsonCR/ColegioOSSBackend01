package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AulaRequest {

    @NotNull(message = "El periodo es obligatorio")
    private Integer periodo;

    @NotNull(message = "El nivel es obligatorio")
    private Nivel nivel;

    @NotNull(message = "El grado es obligatorio")
    private Integer grado;

    @NotBlank(message = "La sección es obligatoria")
    private String seccion;
}