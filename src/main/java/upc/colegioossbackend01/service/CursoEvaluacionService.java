package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.CursoEvaluacionRequest;
import upc.colegioossbackend01.dto.response.CursoEvaluacionResponse;

import java.util.List;

public interface CursoEvaluacionService {

    CursoEvaluacionResponse crear(CursoEvaluacionRequest request);

    List<CursoEvaluacionResponse> listar(Long aulaCursoId, Integer bimestre);

    List<CursoEvaluacionResponse> listarPorAulaCurso(Long aulaCursoId);

    CursoEvaluacionResponse actualizar(Long id, CursoEvaluacionRequest request);

    void desactivar(Long id);

    void activar(Long id);
}