package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.PermisoResponse;
import upc.colegioossbackend01.entity.Permiso;

@Component
public class PermisoMapper {

    public PermisoResponse toResponse(Permiso permiso) {
        return PermisoResponse.builder()
                .id(permiso.getId())
                .codigo(permiso.getCodigo())
                .descripcion(permiso.getDescripcion())
                .activo(permiso.isActivo())
                .build();
    }
}