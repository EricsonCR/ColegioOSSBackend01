package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.AulaCursoResponse;
import upc.colegioossbackend01.entity.AulaCurso;

@Component
public class AulaCursoMapper {

    private final AulaMapper aulaMapper;
    private final CursoMapper cursoMapper;

    public AulaCursoMapper(AulaMapper aulaMapper, CursoMapper cursoMapper) {
        this.aulaMapper = aulaMapper;
        this.cursoMapper = cursoMapper;
    }

    public AulaCursoResponse toResponse(AulaCurso aulaCurso) {
        return AulaCursoResponse.builder()
                .id(aulaCurso.getId())
                .aula(aulaMapper.toResponse(aulaCurso.getAula()))
                .curso(cursoMapper.toResponse(aulaCurso.getCurso()))
                .horasSemana(aulaCurso.getHorasSemana())
                .activo(aulaCurso.isActivo())
                .build();
    }
}