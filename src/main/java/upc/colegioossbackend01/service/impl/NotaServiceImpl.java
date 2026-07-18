package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.NotaRequest;
import upc.colegioossbackend01.dto.response.ConsolidadoNotaItemResponse;
import upc.colegioossbackend01.dto.response.ConsolidadoNotaResponse;
import upc.colegioossbackend01.dto.response.NotaResponse;
import upc.colegioossbackend01.entity.CursoEvaluacion;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.entity.Nota;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.NotaMapper;
import upc.colegioossbackend01.repository.CursoEvaluacionRepository;
import upc.colegioossbackend01.repository.MatriculaRepository;
import upc.colegioossbackend01.repository.NotaRepository;
import upc.colegioossbackend01.service.NotaService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final CursoEvaluacionRepository cursoEvaluacionRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaMapper notaMapper;

    public NotaServiceImpl(NotaRepository notaRepository,
                           CursoEvaluacionRepository cursoEvaluacionRepository,
                           MatriculaRepository matriculaRepository,
                           NotaMapper notaMapper) {
        this.notaRepository = notaRepository;
        this.cursoEvaluacionRepository = cursoEvaluacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.notaMapper = notaMapper;
    }

    @Override
    public NotaResponse registrar(NotaRequest request) {
        CursoEvaluacion cursoEvaluacion = cursoEvaluacionRepository.findById(request.getCursoEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Componente de evaluación no encontrado"));

        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada"));

        Optional<Nota> existente = notaRepository.findByCursoEvaluacionIdAndMatriculaIdAndActivoTrue(
                request.getCursoEvaluacionId(), request.getMatriculaId());

        Nota nota = existente.orElseGet(() -> Nota.builder()
                .cursoEvaluacion(cursoEvaluacion)
                .matricula(matricula)
                .activo(true)
                .build());

        nota.setValor(request.getValor());
        nota.setObservacion(request.getObservacion());

        notaRepository.save(nota);
        return notaMapper.toResponse(nota);
    }

    @Override
    public List<NotaResponse> listarPorMatricula(Long matriculaId) {
        return notaRepository.findByMatriculaIdAndActivoTrue(matriculaId)
                .stream()
                .map(notaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotaResponse> listarPorCursoEvaluacion(Long cursoEvaluacionId) {
        return notaRepository.findByCursoEvaluacionIdAndActivoTrue(cursoEvaluacionId)
                .stream()
                .map(notaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ConsolidadoNotaResponse obtenerConsolidado(Long cursoEvaluacionId) {
        CursoEvaluacion cursoEvaluacion = cursoEvaluacionRepository.findById(cursoEvaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Componente de evaluación no encontrado"));

        var aula = cursoEvaluacion.getAulaCurso().getAula();

        List<Matricula> matriculas = matriculaRepository.findByPeriodoAndNivelAndGradoAndSeccionAndEstado(
                aula.getPeriodo(), aula.getNivel(), aula.getGrado(), aula.getSeccion(), EstadoMatricula.ACTIVA);

        List<Nota> notasExistentes = notaRepository.findByCursoEvaluacionIdAndActivoTrue(cursoEvaluacionId);
        Map<Long, Nota> notasPorMatricula = notasExistentes.stream()
                .collect(Collectors.toMap(n -> n.getMatricula().getId(), n -> n));

        List<ConsolidadoNotaItemResponse> items = matriculas.stream()
                .map(matricula -> {
                    Nota nota = notasPorMatricula.get(matricula.getId());
                    return ConsolidadoNotaItemResponse.builder()
                            .matriculaId(matricula.getId())
                            .estudianteNombre(matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos())
                            .estudianteDocumento(matricula.getEstudiante().getNumeroDocumento())
                            .notaId(nota != null ? nota.getId() : null)
                            .valor(nota != null ? nota.getValor() : null)
                            .observacion(nota != null ? nota.getObservacion() : null)
                            .tieneNota(nota != null)
                            .build();
                })
                .collect(Collectors.toList());

        return ConsolidadoNotaResponse.builder()
                .cursoEvaluacionId(cursoEvaluacion.getId())
                .componenteNombre(cursoEvaluacion.getNombre())
                .porcentaje(cursoEvaluacion.getPorcentaje())
                .bimestre(cursoEvaluacion.getBimestre())
                .cursoNombre(cursoEvaluacion.getAulaCurso().getCurso().getNombre())
                .aulaDescripcion(aula.getNivel() + " " + aula.getGrado() + aula.getSeccion() + " - " + aula.getPeriodo())
                .estudiantes(items)
                .build();
    }
}