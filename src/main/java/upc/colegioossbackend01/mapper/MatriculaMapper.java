package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.ApoderadoAsignadoResponse;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.entity.Matricula;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatriculaMapper {

    private final EstudianteMapper estudianteMapper;
    private final ApoderadoMapper apoderadoMapper;

    public MatriculaMapper(EstudianteMapper estudianteMapper, ApoderadoMapper apoderadoMapper) {
        this.estudianteMapper = estudianteMapper;
        this.apoderadoMapper = apoderadoMapper;
    }

    public MatriculaResponse toResponse(Matricula matricula, List<EstudianteApoderado> apoderados) {
        List<ApoderadoAsignadoResponse> apoderadosResponse = apoderados.stream()
                .map(ea -> ApoderadoAsignadoResponse.builder()
                        .apoderado(apoderadoMapper.toResponse(ea.getApoderado()))
                        .parentesco(ea.getParentesco())
                        .esPrincipal(ea.isEsPrincipal())
                        .build())
                .collect(Collectors.toList());

        return MatriculaResponse.builder()
                .id(matricula.getId())
                .estudiante(estudianteMapper.toResponse(matricula.getEstudiante()))
                .periodo(matricula.getPeriodo())
                .nivel(matricula.getNivel())
                .grado(matricula.getGrado())
                .seccion(matricula.getSeccion())
                .fechaMatricula(matricula.getFechaMatricula())
                .tipoMatricula(matricula.getTipoMatricula())
                .estado(matricula.getEstado())
                .apoderados(apoderadosResponse)
                .build();
    }
}