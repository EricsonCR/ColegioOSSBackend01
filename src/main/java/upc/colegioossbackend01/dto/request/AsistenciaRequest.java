package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class AsistenciaRequest {

    @NotNull(message = "La clase es obligatoria")
    private Long claseId;

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El estado es obligatorio")
    private EstadoAsistencia estado;

    private String observacion;
}