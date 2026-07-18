package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class NotaRequest {

    @NotNull(message = "El componente de evaluación es obligatorio")
    private Long cursoEvaluacionId;

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El valor de la nota es obligatorio")
    @DecimalMin(value = "0.0", message = "La nota no puede ser negativa")
    @DecimalMax(value = "20.0", message = "La nota no puede superar 20")
    private BigDecimal valor;

    private String observacion;
}