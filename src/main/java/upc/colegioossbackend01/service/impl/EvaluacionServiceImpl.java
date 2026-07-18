package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.EvaluacionRequest;
import upc.colegioossbackend01.dto.response.EvaluacionResponse;
import upc.colegioossbackend01.entity.Evaluacion;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.mapper.EvaluacionMapper;
import upc.colegioossbackend01.repository.EvaluacionRepository;
import upc.colegioossbackend01.service.EvaluacionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionMapper evaluacionMapper;

    public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository, EvaluacionMapper evaluacionMapper) {
        this.evaluacionRepository = evaluacionRepository;
        this.evaluacionMapper = evaluacionMapper;
    }

    @Override
    public EvaluacionResponse crear(EvaluacionRequest request) {
        if (evaluacionRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe una evaluación con ese código");
        }

        Evaluacion evaluacion = Evaluacion.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .activo(true)
                .build();

        evaluacionRepository.save(evaluacion);
        return evaluacionMapper.toResponse(evaluacion);
    }

    @Override
    public List<EvaluacionResponse> listar(boolean incluirInactivos) {
        List<Evaluacion> evaluaciones = incluirInactivos
                ? evaluacionRepository.findAll()
                : evaluacionRepository.findByActivoTrue();

        return evaluaciones.stream()
                .map(evaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }
}