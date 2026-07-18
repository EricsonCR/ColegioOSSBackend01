package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.AulaResponse;
import upc.colegioossbackend01.entity.Aula;

@Component
public class AulaMapper {

    public AulaResponse toResponse(Aula aula) {
        return AulaResponse.builder()
                .id(aula.getId())
                .periodo(aula.getPeriodo())
                .nivel(aula.getNivel())
                .grado(aula.getGrado())
                .seccion(aula.getSeccion())
                .activo(aula.isActivo())
                .build();
    }
}