package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.ClaseRequest;
import upc.colegioossbackend01.dto.response.ClaseResponse;
import upc.colegioossbackend01.entity.AulaCurso;
import upc.colegioossbackend01.entity.Clase;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.ClaseMapper;
import upc.colegioossbackend01.repository.AulaCursoRepository;
import upc.colegioossbackend01.repository.ClaseRepository;
import upc.colegioossbackend01.service.ClaseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaseServiceImpl implements ClaseService {

    private final ClaseRepository claseRepository;
    private final AulaCursoRepository aulaCursoRepository;
    private final ClaseMapper claseMapper;

    public ClaseServiceImpl(ClaseRepository claseRepository, AulaCursoRepository aulaCursoRepository, ClaseMapper claseMapper) {
        this.claseRepository = claseRepository;
        this.aulaCursoRepository = aulaCursoRepository;
        this.claseMapper = claseMapper;
    }

    @Override
    public ClaseResponse crear(ClaseRequest request) {
        AulaCurso aulaCurso = aulaCursoRepository.findById(request.getAulaCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso-aula no encontrado"));

        Clase clase = Clase.builder()
                .aulaCurso(aulaCurso)
                .fecha(request.getFecha())
                .tema(request.getTema())
                .observacion(request.getObservacion())
                .activo(true)
                .build();

        claseRepository.save(clase);
        return claseMapper.toResponse(clase);
    }

    @Override
    public List<ClaseResponse> listarPorAulaCurso(Long aulaCursoId) {
        return claseRepository.findByAulaCursoIdAndActivoTrueOrderByFechaDesc(aulaCursoId)
                .stream()
                .map(claseMapper::toResponse)
                .collect(Collectors.toList());
    }
}