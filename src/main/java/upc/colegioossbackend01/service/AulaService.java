package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AulaRequest;
import upc.colegioossbackend01.dto.response.AulaResponse;

import java.util.List;

public interface AulaService {

    AulaResponse crear(AulaRequest request);

    List<AulaResponse> listar(boolean incluirInactivos);

    AulaResponse actualizar(Long id, AulaRequest request);

    void desactivar(Long id);

    void activar(Long id);
}