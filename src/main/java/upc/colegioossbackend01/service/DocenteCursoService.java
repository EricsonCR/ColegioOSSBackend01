package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.DocenteCursoRequest;
import upc.colegioossbackend01.dto.response.DocenteCursoResponse;

import java.util.List;

public interface DocenteCursoService {

    DocenteCursoResponse crear(DocenteCursoRequest request);

    List<DocenteCursoResponse> listarPorDocente(Long usuarioId);

    List<DocenteCursoResponse> listarPorDocenteUsername(String username);

    List<DocenteCursoResponse> listarPorAulaCurso(Long aulaCursoId);
}