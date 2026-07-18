package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.NotaResponse;
import upc.colegioossbackend01.entity.Nota;

@Component
public class NotaMapper {

    public NotaResponse toResponse(Nota nota) {
        return NotaResponse.builder()
                .id(nota.getId())
                .cursoEvaluacionId(nota.getCursoEvaluacion().getId())
                .componenteNombre(nota.getCursoEvaluacion().getNombre())
                .porcentaje(nota.getCursoEvaluacion().getPorcentaje())
                .matriculaId(nota.getMatricula().getId())
                .estudianteNombre(nota.getMatricula().getEstudiante().getNombres() + " " + nota.getMatricula().getEstudiante().getApellidos())
                .valor(nota.getValor())
                .observacion(nota.getObservacion())
                .activo(nota.isActivo())
                .build();
    }
}