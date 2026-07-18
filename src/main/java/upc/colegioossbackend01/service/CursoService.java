package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.CursoRequest;
import upc.colegioossbackend01.dto.response.CursoResponse;

import java.util.List;

public interface CursoService {

    CursoResponse crear(CursoRequest request);

    List<CursoResponse> listar(boolean incluirInactivos);

    CursoResponse actualizar(Long id, CursoRequest request);

    void desactivar(Long id);

    void activar(Long id);
}