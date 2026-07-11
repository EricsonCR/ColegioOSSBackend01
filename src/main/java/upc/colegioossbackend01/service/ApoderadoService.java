package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.ApoderadoRequest;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;

import java.util.List;
import java.util.Optional;

public interface ApoderadoService {

    ApoderadoResponse crear(ApoderadoRequest request);

    Optional<ApoderadoResponse> buscarPorDocumento(String numeroDocumento);

    List<ApoderadoResponse> listar(boolean incluirInactivos);

    ApoderadoResponse obtenerPorId(Long id);

    ApoderadoResponse actualizar(Long id, ApoderadoRequest request);

    void desactivar(Long id);

    void activar(Long id);
}