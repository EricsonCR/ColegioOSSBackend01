package upc.colegioossbackend01.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.enums.TipoMatricula;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatricularRequest {

    private Long estudianteId;

    @Valid
    private EstudianteRequest estudianteNuevo;

    @NotEmpty(message = "Debe indicar al menos un apoderado")
    @Valid
    private List<ApoderadoAsignacionRequest> apoderados;

    @NotNull(message = "El periodo es obligatorio")
    private Integer periodo;

    @NotNull(message = "El nivel es obligatorio")
    private Nivel nivel;

    @NotNull(message = "El grado es obligatorio")
    private Integer grado;

    @NotNull(message = "La sección es obligatoria")
    private String seccion;

    @NotNull(message = "La fecha de matrícula es obligatoria")
    private LocalDate fechaMatricula;

    @NotNull(message = "El tipo de matrícula es obligatorio")
    private TipoMatricula tipoMatricula;
}