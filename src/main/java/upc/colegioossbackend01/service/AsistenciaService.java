package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AsistenciaRequest;
import upc.colegioossbackend01.dto.response.AsistenciaResponse;
import upc.colegioossbackend01.dto.response.ConsolidadoAsistenciaResponse;

public interface AsistenciaService {

    AsistenciaResponse registrar(AsistenciaRequest request);

    ConsolidadoAsistenciaResponse obtenerConsolidado(Long claseId);
}