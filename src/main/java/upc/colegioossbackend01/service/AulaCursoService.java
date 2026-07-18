package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AulaCursoRequest;
import upc.colegioossbackend01.dto.response.AulaCursoResponse;

import java.util.List;

public interface AulaCursoService {

    AulaCursoResponse crear(AulaCursoRequest request);

    List<AulaCursoResponse> listarPorAula(Long aulaId);
}