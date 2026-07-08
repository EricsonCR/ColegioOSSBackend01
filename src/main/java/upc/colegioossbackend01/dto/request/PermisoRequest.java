package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class PermisoRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}