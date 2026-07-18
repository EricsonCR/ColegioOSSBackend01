package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.EvaluacionRequest;
import upc.colegioossbackend01.dto.response.EvaluacionResponse;

import java.util.List;

public interface EvaluacionService {

    EvaluacionResponse crear(EvaluacionRequest request);

    List<EvaluacionResponse> listar(boolean incluirInactivos);
}