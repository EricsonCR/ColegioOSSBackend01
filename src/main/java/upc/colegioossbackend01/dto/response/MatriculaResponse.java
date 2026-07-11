package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.enums.TipoMatricula;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponse {

    private Long id;
    private EstudianteResponse estudiante;
    private Integer periodo;
    private Nivel nivel;
    private Integer grado;
    private String seccion;
    private LocalDate fechaMatricula;
    private TipoMatricula tipoMatricula;
    private EstadoMatricula estado;
    private List<ApoderadoAsignadoResponse> apoderados;
}