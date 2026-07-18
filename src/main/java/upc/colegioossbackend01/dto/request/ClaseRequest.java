package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseRequest {

    @NotNull(message = "El curso-aula es obligatorio")
    private Long aulaCursoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    private String tema;
    private String observacion;
}