package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AsignarPermisosRequest;
import upc.colegioossbackend01.dto.request.RolRequest;
import upc.colegioossbackend01.dto.response.RolResponse;

import java.util.List;

public interface RolService {

    RolResponse crear(RolRequest request);

    List<RolResponse> listar(boolean incluirInactivos);

    RolResponse obtenerPorId(Long id);

    RolResponse actualizar(Long id, RolRequest request);

    void desactivar(Long id);

    void activar(Long id);

    RolResponse asignarPermisos(Long rolId, AsignarPermisosRequest request);
}