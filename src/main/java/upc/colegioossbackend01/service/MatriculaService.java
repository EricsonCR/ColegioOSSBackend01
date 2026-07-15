package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.MatriculaRequest;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;

import java.util.List;

public interface MatriculaService {

    List<MatriculaResponse> listar(Integer periodo, Nivel nivel, Integer grado, EstadoMatricula estado);

    MatriculaResponse obtenerPorId(Long id);

    MatriculaResponse actualizar(Long id, MatriculaRequest request);

    MatriculaResponse cambiarEstado(Long id, EstadoMatricula nuevoEstado);
}