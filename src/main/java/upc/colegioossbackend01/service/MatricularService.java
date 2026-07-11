package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.MatricularRequest;
import upc.colegioossbackend01.dto.response.MatriculaResponse;

public interface MatricularService {

    MatriculaResponse matricular(MatricularRequest request);
}