package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AsignarApoderadoRequest;
import upc.colegioossbackend01.entity.EstudianteApoderado;

import java.util.List;

public interface EstudianteApoderadoService {

    EstudianteApoderado asignarApoderado(Long estudianteId, AsignarApoderadoRequest request);

    List<EstudianteApoderado> listarApoderadosDeEstudiante(Long estudianteId);

    void quitarApoderado(Long estudianteId, Long apoderadoId);
}