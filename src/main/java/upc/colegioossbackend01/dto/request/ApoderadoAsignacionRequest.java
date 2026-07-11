package upc.colegioossbackend01.dto.request;

import jakarta.validation.Valid;
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
public class ApoderadoAsignacionRequest {

    private Long apoderadoId;

    @Valid
    private ApoderadoRequest apoderadoNuevo;

    @NotBlank(message = "El parentesco es obligatorio")
    private String parentesco;

    @Builder.Default
    private boolean esPrincipal = false;
}