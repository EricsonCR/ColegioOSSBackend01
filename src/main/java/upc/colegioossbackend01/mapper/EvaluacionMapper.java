package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.EvaluacionResponse;
import upc.colegioossbackend01.entity.Evaluacion;

@Component
public class EvaluacionMapper {

    public EvaluacionResponse toResponse(Evaluacion evaluacion) {
        return EvaluacionResponse.builder()
                .id(evaluacion.getId())
                .codigo(evaluacion.getCodigo())
                .nombre(evaluacion.getNombre())
                .activo(evaluacion.isActivo())
                .build();
    }
}