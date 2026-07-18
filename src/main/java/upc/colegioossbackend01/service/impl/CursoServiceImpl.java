package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.CursoRequest;
import upc.colegioossbackend01.dto.response.CursoResponse;
import upc.colegioossbackend01.entity.Curso;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.CursoMapper;
import upc.colegioossbackend01.repository.CursoRepository;
import upc.colegioossbackend01.service.CursoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;

    public CursoServiceImpl(CursoRepository cursoRepository, CursoMapper cursoMapper) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
    }

    @Override
    public CursoResponse crear(CursoRequest request) {
        if (cursoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un curso con ese código");
        }

        Curso curso = Curso.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .activo(true)
                .build();

        cursoRepository.save(curso);
        return cursoMapper.toResponse(curso);
    }

    @Override
    public List<CursoResponse> listar(boolean incluirInactivos) {
        List<Curso> cursos = incluirInactivos
                ? cursoRepository.findAll()
                : cursoRepository.findByActivoTrue();

        return cursos.stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CursoResponse actualizar(Long id, CursoRequest request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (!curso.getCodigo().equals(request.getCodigo())) {
            throw new BusinessException("El código del curso no puede modificarse");
        }

        curso.setNombre(request.getNombre());
        cursoRepository.save(curso);
        return cursoMapper.toResponse(curso);
    }

    @Override
    public void desactivar(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        curso.setActivo(false);
        cursoRepository.save(curso);
    }

    @Override
    public void activar(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        curso.setActivo(true);
        cursoRepository.save(curso);
    }
}