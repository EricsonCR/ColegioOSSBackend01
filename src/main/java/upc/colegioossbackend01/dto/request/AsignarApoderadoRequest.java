package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AsignarApoderadoRequest {

    @NotNull(message = "El apoderado es obligatorio")
    private Long apoderadoId;

    @NotBlank(message = "El parentesco es obligatorio")
    private String parentesco;

    @Builder.Default
    private boolean esPrincipal = false;
}