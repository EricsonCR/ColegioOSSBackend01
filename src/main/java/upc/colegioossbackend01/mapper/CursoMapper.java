package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.CursoResponse;
import upc.colegioossbackend01.entity.Curso;

@Component
public class CursoMapper {

    public CursoResponse toResponse(Curso curso) {
        return CursoResponse.builder()
                .id(curso.getId())
                .codigo(curso.getCodigo())
                .nombre(curso.getNombre())
                .activo(curso.isActivo())
                .build();
    }
}