package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.NotaRequest;
import upc.colegioossbackend01.dto.response.ConsolidadoNotaResponse;
import upc.colegioossbackend01.dto.response.NotaResponse;

import java.util.List;

public interface NotaService {

    NotaResponse registrar(NotaRequest request);

    List<NotaResponse> listarPorMatricula(Long matriculaId);

    List<NotaResponse> listarPorCursoEvaluacion(Long cursoEvaluacionId);

    ConsolidadoNotaResponse obtenerConsolidado(Long cursoEvaluacionId);
}