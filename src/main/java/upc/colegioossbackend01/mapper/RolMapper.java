package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.RolResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.entity.Rol;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolMapper {

    private final PermisoMapper permisoMapper;

    public RolMapper(PermisoMapper permisoMapper) {
        this.permisoMapper = permisoMapper;
    }

    public RolResponse toResponse(Rol rol) {
        List<upc.colegioossbackend01.dto.response.PermisoResponse> permisos = rol.getPermisos() == null
                ? List.of()
                : rol.getPermisos().stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());

        return RolResponse.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.isActivo())
                .permisos(permisos)
                .build();
    }
}