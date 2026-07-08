package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.PermisoRequest;
import upc.colegioossbackend01.dto.response.PermisoResponse;

import java.util.List;

public interface PermisoService {

    PermisoResponse crear(PermisoRequest request);

    List<PermisoResponse> listar(boolean incluirInactivos);

    PermisoResponse obtenerPorId(Long id);

    PermisoResponse actualizar(Long id, PermisoRequest request);

    void desactivar(Long id);

    void activar(Long id);
}