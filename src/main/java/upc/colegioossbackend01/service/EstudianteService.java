package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.EstudianteRequest;
import upc.colegioossbackend01.dto.response.EstudianteResponse;

import java.util.List;
import java.util.Optional;

public interface EstudianteService {

    EstudianteResponse crear(EstudianteRequest request);

    Optional<EstudianteResponse> buscarPorDocumento(String numeroDocumento);

    List<EstudianteResponse> listar(boolean incluirInactivos);

    EstudianteResponse obtenerPorId(Long id);

    EstudianteResponse actualizar(Long id, EstudianteRequest request);

    void desactivar(Long id);

    void activar(Long id);
}