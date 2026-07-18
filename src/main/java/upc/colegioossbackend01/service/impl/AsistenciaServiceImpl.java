package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.AsistenciaRequest;
import upc.colegioossbackend01.dto.response.AsistenciaResponse;
import upc.colegioossbackend01.dto.response.ConsolidadoAsistenciaItemResponse;
import upc.colegioossbackend01.dto.response.ConsolidadoAsistenciaResponse;
import upc.colegioossbackend01.entity.*;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.AsistenciaMapper;
import upc.colegioossbackend01.repository.*;
import upc.colegioossbackend01.service.AsistenciaService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final ClaseRepository claseRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaMapper asistenciaMapper;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                 ClaseRepository claseRepository,
                                 MatriculaRepository matriculaRepository,
                                 AsistenciaMapper asistenciaMapper) {
        this.asistenciaRepository = asistenciaRepository;
        this.claseRepository = claseRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaMapper = asistenciaMapper;
    }

    @Override
    public AsistenciaResponse registrar(AsistenciaRequest request) {
        Clase clase = claseRepository.findById(request.getClaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));

        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada"));

        Optional<Asistencia> existente = asistenciaRepository
                .findByClaseIdAndMatriculaIdAndActivoTrue(request.getClaseId(), request.getMatriculaId());

        Asistencia asistencia = existente.orElseGet(() -> Asistencia.builder()
                .clase(clase)
                .matricula(matricula)
                .activo(true)
                .build());

        asistencia.setEstado(request.getEstado());
        asistencia.setObservacion(request.getObservacion());

        asistenciaRepository.save(asistencia);
        return asistenciaMapper.toResponse(asistencia);
    }

    @Override
    public ConsolidadoAsistenciaResponse obtenerConsolidado(Long claseId) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));

        Aula aula = clase.getAulaCurso().getAula();

        List<Matricula> matriculas = matriculaRepository.findByPeriodoAndNivelAndGradoAndSeccionAndEstado(
                aula.getPeriodo(), aula.getNivel(), aula.getGrado(), aula.getSeccion(), EstadoMatricula.ACTIVA);

        List<Asistencia> asistenciasExistentes = asistenciaRepository.findByClaseIdAndActivoTrue(claseId);
        Map<Long, Asistencia> asistenciasPorMatricula = asistenciasExistentes.stream()
                .collect(Collectors.toMap(a -> a.getMatricula().getId(), a -> a));

        List<ConsolidadoAsistenciaItemResponse> items = matriculas.stream()
                .map(matricula -> {
                    Asistencia asistencia = asistenciasPorMatricula.get(matricula.getId());
                    return ConsolidadoAsistenciaItemResponse.builder()
                            .matriculaId(matricula.getId())
                            .estudianteNombre(matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos())
                            .estudianteDocumento(matricula.getEstudiante().getNumeroDocumento())
                            .asistenciaId(asistencia != null ? asistencia.getId() : null)
                            .estado(asistencia != null ? asistencia.getEstado() : null)
                            .observacion(asistencia != null ? asistencia.getObservacion() : null)
                            .registrado(asistencia != null)
                            .build();
                })
                .collect(Collectors.toList());

        return ConsolidadoAsistenciaResponse.builder()
                .claseId(clase.getId())
                .fecha(clase.getFecha())
                .tema(clase.getTema())
                .cursoNombre(clase.getAulaCurso().getCurso().getNombre())
                .aulaDescripcion(aula.getNivel() + " " + aula.getGrado() + aula.getSeccion() + " - " + aula.getPeriodo())
                .estudiantes(items)
                .build();
    }
}