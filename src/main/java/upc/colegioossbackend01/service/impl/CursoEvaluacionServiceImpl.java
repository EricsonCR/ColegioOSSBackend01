package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.CursoEvaluacionRequest;
import upc.colegioossbackend01.dto.response.CursoEvaluacionResponse;
import upc.colegioossbackend01.entity.AulaCurso;
import upc.colegioossbackend01.entity.CursoEvaluacion;
import upc.colegioossbackend01.entity.Evaluacion;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.CursoEvaluacionMapper;
import upc.colegioossbackend01.repository.AulaCursoRepository;
import upc.colegioossbackend01.repository.CursoEvaluacionRepository;
import upc.colegioossbackend01.repository.EvaluacionRepository;
import upc.colegioossbackend01.service.CursoEvaluacionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoEvaluacionServiceImpl implements CursoEvaluacionService {

    private final CursoEvaluacionRepository cursoEvaluacionRepository;
    private final AulaCursoRepository aulaCursoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final CursoEvaluacionMapper cursoEvaluacionMapper;

    public CursoEvaluacionServiceImpl(CursoEvaluacionRepository cursoEvaluacionRepository,
                                      AulaCursoRepository aulaCursoRepository,
                                      EvaluacionRepository evaluacionRepository,
                                      CursoEvaluacionMapper cursoEvaluacionMapper) {
        this.cursoEvaluacionRepository = cursoEvaluacionRepository;
        this.aulaCursoRepository = aulaCursoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.cursoEvaluacionMapper = cursoEvaluacionMapper;
    }

    @Override
    public CursoEvaluacionResponse crear(CursoEvaluacionRequest request) {
        AulaCurso aulaCurso = aulaCursoRepository.findById(request.getAulaCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso-aula no encontrado"));

        Evaluacion evaluacion = evaluacionRepository.findById(request.getEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        List<CursoEvaluacion> existentes = cursoEvaluacionRepository
                .findByAulaCursoIdAndBimestreAndActivoTrue(request.getAulaCursoId(), request.getBimestre());

        boolean yaExiste = existentes.stream()
                .anyMatch(ce -> ce.getEvaluacion().getId().equals(request.getEvaluacionId())
                        && ((ce.getNombre() == null && request.getNombre() == null)
                        || (ce.getNombre() != null && ce.getNombre().equalsIgnoreCase(request.getNombre()))));

        if (yaExiste) {
            throw new BusinessException("Ya existe un componente de evaluación con ese nombre y tipo para este curso y bimestre");
        }

        int sumaActual = existentes.stream().mapToInt(CursoEvaluacion::getPorcentaje).sum();

        if (sumaActual + request.getPorcentaje() > 100) {
            throw new BusinessException("La suma de porcentajes para este curso y bimestre superaría el 100% (actual: " + sumaActual + "%)");
        }

        CursoEvaluacion cursoEvaluacion = CursoEvaluacion.builder()
                .aulaCurso(aulaCurso)
                .bimestre(request.getBimestre())
                .evaluacion(evaluacion)
                .nombre(request.getNombre())
                .porcentaje(request.getPorcentaje())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .build();

        cursoEvaluacionRepository.save(cursoEvaluacion);
        return cursoEvaluacionMapper.toResponse(cursoEvaluacion);
    }

    @Override
    public List<CursoEvaluacionResponse> listar(Long aulaCursoId, Integer bimestre) {
        return cursoEvaluacionRepository.findByAulaCursoIdAndBimestreAndActivoTrue(aulaCursoId, bimestre)
                .stream()
                .map(cursoEvaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoEvaluacionResponse> listarPorAulaCurso(Long aulaCursoId) {
        return cursoEvaluacionRepository.findByAulaCursoIdAndActivoTrue(aulaCursoId)
                .stream()
                .map(cursoEvaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CursoEvaluacionResponse actualizar(Long id, CursoEvaluacionRequest request) {
        CursoEvaluacion cursoEvaluacion = cursoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente de evaluación no encontrado"));

        Evaluacion evaluacion = evaluacionRepository.findById(request.getEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        List<CursoEvaluacion> otros = cursoEvaluacionRepository
                .findByAulaCursoIdAndBimestreAndActivoTrueAndIdNot(request.getAulaCursoId(), request.getBimestre(), id);

        int sumaOtros = otros.stream().mapToInt(CursoEvaluacion::getPorcentaje).sum();

        if (sumaOtros + request.getPorcentaje() > 100) {
            throw new BusinessException("La suma de porcentajes para este curso y bimestre superaría el 100% (otros: " + sumaOtros + "%)");
        }

        cursoEvaluacion.setBimestre(request.getBimestre());
        cursoEvaluacion.setEvaluacion(evaluacion);
        cursoEvaluacion.setNombre(request.getNombre());
        cursoEvaluacion.setPorcentaje(request.getPorcentaje());
        cursoEvaluacion.setFechaInicio(request.getFechaInicio());
        cursoEvaluacion.setFechaFin(request.getFechaFin());

        cursoEvaluacionRepository.save(cursoEvaluacion);
        return cursoEvaluacionMapper.toResponse(cursoEvaluacion);
    }

    @Override
    public void desactivar(Long id) {
        CursoEvaluacion cursoEvaluacion = cursoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente de evaluación no encontrado"));
        cursoEvaluacion.setActivo(false);
        cursoEvaluacionRepository.save(cursoEvaluacion);
    }

    @Override
    public void activar(Long id) {
        CursoEvaluacion cursoEvaluacion = cursoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Componente de evaluación no encontrado"));
        cursoEvaluacion.setActivo(true);
        cursoEvaluacionRepository.save(cursoEvaluacion);
    }
}