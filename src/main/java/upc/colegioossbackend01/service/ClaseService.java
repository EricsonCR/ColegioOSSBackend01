package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.ClaseRequest;
import upc.colegioossbackend01.dto.response.ClaseResponse;

import java.util.List;

public interface ClaseService {

    ClaseResponse crear(ClaseRequest request);

    List<ClaseResponse> listarPorAulaCurso(Long aulaCursoId);
}