package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.DocenteCursoResponse;
import upc.colegioossbackend01.entity.DocenteCurso;

@Component
public class DocenteCursoMapper {

    private final AulaCursoMapper aulaCursoMapper;

    public DocenteCursoMapper(AulaCursoMapper aulaCursoMapper) {
        this.aulaCursoMapper = aulaCursoMapper;
    }

    public DocenteCursoResponse toResponse(DocenteCurso docenteCurso) {
        return DocenteCursoResponse.builder()
                .id(docenteCurso.getId())
                .aulaCurso(aulaCursoMapper.toResponse(docenteCurso.getAulaCurso()))
                .usuarioId(docenteCurso.getUsuario().getId())
                .docenteNombre(docenteCurso.getUsuario().getNombreCompleto())
                .activo(docenteCurso.isActivo())
                .build();
    }
}