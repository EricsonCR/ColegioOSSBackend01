package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.AulaCursoRequest;
import upc.colegioossbackend01.dto.response.AulaCursoResponse;
import upc.colegioossbackend01.entity.Aula;
import upc.colegioossbackend01.entity.AulaCurso;
import upc.colegioossbackend01.entity.Curso;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.AulaCursoMapper;
import upc.colegioossbackend01.repository.AulaCursoRepository;
import upc.colegioossbackend01.repository.AulaRepository;
import upc.colegioossbackend01.repository.CursoRepository;
import upc.colegioossbackend01.service.AulaCursoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AulaCursoServiceImpl implements AulaCursoService {

    private final AulaCursoRepository aulaCursoRepository;
    private final AulaRepository aulaRepository;
    private final CursoRepository cursoRepository;
    private final AulaCursoMapper aulaCursoMapper;

    public AulaCursoServiceImpl(AulaCursoRepository aulaCursoRepository,
                                AulaRepository aulaRepository,
                                CursoRepository cursoRepository,
                                AulaCursoMapper aulaCursoMapper) {
        this.aulaCursoRepository = aulaCursoRepository;
        this.aulaRepository = aulaRepository;
        this.cursoRepository = cursoRepository;
        this.aulaCursoMapper = aulaCursoMapper;
    }

    @Override
    public AulaCursoResponse crear(AulaCursoRequest request) {
        Aula aula = aulaRepository.findById(request.getAulaId())
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (aulaCursoRepository.existsByAulaIdAndCursoId(request.getAulaId(), request.getCursoId())) {
            throw new BusinessException("Este curso ya está asignado a esta aula");
        }

        AulaCurso aulaCurso = AulaCurso.builder()
                .aula(aula)
                .curso(curso)
                .horasSemana(request.getHorasSemana())
                .activo(true)
                .build();

        aulaCursoRepository.save(aulaCurso);
        return aulaCursoMapper.toResponse(aulaCurso);
    }

    @Override
    public List<AulaCursoResponse> listarPorAula(Long aulaId) {
        return aulaCursoRepository.findByAulaIdAndActivoTrue(aulaId)
                .stream()
                .map(aulaCursoMapper::toResponse)
                .collect(Collectors.toList());
    }
}