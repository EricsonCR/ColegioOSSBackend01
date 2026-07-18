package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.ClaseResponse;
import upc.colegioossbackend01.entity.Clase;

@Component
public class ClaseMapper {

    public ClaseResponse toResponse(Clase clase) {
        return ClaseResponse.builder()
                .id(clase.getId())
                .aulaCursoId(clase.getAulaCurso().getId())
                .cursoNombre(clase.getAulaCurso().getCurso().getNombre())
                .fecha(clase.getFecha())
                .tema(clase.getTema())
                .observacion(clase.getObservacion())
                .activo(clase.isActivo())
                .build();
    }
}