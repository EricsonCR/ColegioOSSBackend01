package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.CursoEvaluacionResponse;
import upc.colegioossbackend01.entity.CursoEvaluacion;

@Component
public class CursoEvaluacionMapper {

    private final EvaluacionMapper evaluacionMapper;

    public CursoEvaluacionMapper(EvaluacionMapper evaluacionMapper) {
        this.evaluacionMapper = evaluacionMapper;
    }

    public CursoEvaluacionResponse toResponse(CursoEvaluacion cursoEvaluacion) {
        return CursoEvaluacionResponse.builder()
                .id(cursoEvaluacion.getId())
                .aulaCursoId(cursoEvaluacion.getAulaCurso().getId())
                .bimestre(cursoEvaluacion.getBimestre())
                .evaluacion(evaluacionMapper.toResponse(cursoEvaluacion.getEvaluacion()))
                .nombre(cursoEvaluacion.getNombre())
                .porcentaje(cursoEvaluacion.getPorcentaje())
                .fechaInicio(cursoEvaluacion.getFechaInicio())
                .fechaFin(cursoEvaluacion.getFechaFin())
                .activo(cursoEvaluacion.isActivo())
                .build();
    }
}