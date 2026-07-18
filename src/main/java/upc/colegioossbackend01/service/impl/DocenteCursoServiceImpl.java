package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.DocenteCursoRequest;
import upc.colegioossbackend01.dto.response.DocenteCursoResponse;
import upc.colegioossbackend01.entity.AulaCurso;
import upc.colegioossbackend01.entity.DocenteCurso;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.DocenteCursoMapper;
import upc.colegioossbackend01.repository.AulaCursoRepository;
import upc.colegioossbackend01.repository.DocenteCursoRepository;
import upc.colegioossbackend01.repository.UsuarioRepository;
import upc.colegioossbackend01.service.DocenteCursoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocenteCursoServiceImpl implements DocenteCursoService {

    private final DocenteCursoRepository docenteCursoRepository;
    private final AulaCursoRepository aulaCursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DocenteCursoMapper docenteCursoMapper;

    public DocenteCursoServiceImpl(DocenteCursoRepository docenteCursoRepository,
                                   AulaCursoRepository aulaCursoRepository,
                                   UsuarioRepository usuarioRepository,
                                   DocenteCursoMapper docenteCursoMapper) {
        this.docenteCursoRepository = docenteCursoRepository;
        this.aulaCursoRepository = aulaCursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.docenteCursoMapper = docenteCursoMapper;
    }

    @Override
    public DocenteCursoResponse crear(DocenteCursoRequest request) {
        AulaCurso aulaCurso = aulaCursoRepository.findById(request.getAulaCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso-aula no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() == null || !"DOCENTE".equals(usuario.getRol().getNombre())) {
            throw new BusinessException("El usuario seleccionado no tiene el rol DOCENTE");
        }

        if (docenteCursoRepository.existsByAulaCursoIdAndUsuarioId(request.getAulaCursoId(), request.getUsuarioId())) {
            throw new BusinessException("Este docente ya está asignado a este curso-aula");
        }

        DocenteCurso docenteCurso = DocenteCurso.builder()
                .aulaCurso(aulaCurso)
                .usuario(usuario)
                .activo(true)
                .build();

        docenteCursoRepository.save(docenteCurso);
        return docenteCursoMapper.toResponse(docenteCurso);
    }

    @Override
    public List<DocenteCursoResponse> listarPorDocente(Long usuarioId) {
        return docenteCursoRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .stream()
                .map(docenteCursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocenteCursoResponse> listarPorDocenteUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return listarPorDocente(usuario.getId());
    }

    @Override
    public List<DocenteCursoResponse> listarPorAulaCurso(Long aulaCursoId) {
        return docenteCursoRepository.findByAulaCursoIdAndActivoTrue(aulaCursoId)
                .stream()
                .map(docenteCursoMapper::toResponse)
                .collect(Collectors.toList());
    }
}