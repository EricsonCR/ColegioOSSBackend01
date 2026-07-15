package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.MatriculaRequest;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.MatriculaMapper;
import upc.colegioossbackend01.repository.MatriculaRepository;
import upc.colegioossbackend01.service.EstudianteApoderadoService;
import upc.colegioossbackend01.service.MatriculaService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteApoderadoService estudianteApoderadoService;
    private final MatriculaMapper matriculaMapper;

    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                EstudianteApoderadoService estudianteApoderadoService,
                                MatriculaMapper matriculaMapper) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteApoderadoService = estudianteApoderadoService;
        this.matriculaMapper = matriculaMapper;
    }

    @Override
    public List<MatriculaResponse> listar(Integer periodo, Nivel nivel, Integer grado, EstadoMatricula estado) {
        List<Matricula> matriculas = matriculaRepository.buscarConFiltros(periodo, nivel, grado, estado);

        return matriculas.stream()
                .map(this::toResponseConApoderados)
                .collect(Collectors.toList());
    }

    @Override
    public MatriculaResponse obtenerPorId(Long id) {
        Matricula matricula = buscarPorId(id);
        return toResponseConApoderados(matricula);
    }

    @Override
    public MatriculaResponse actualizar(Long id, MatriculaRequest request) {
        Matricula matricula = buscarPorId(id);

        matricula.setNivel(request.getNivel());
        matricula.setGrado(request.getGrado());
        matricula.setSeccion(request.getSeccion());
        matricula.setFechaMatricula(request.getFechaMatricula());
        matricula.setTipoMatricula(request.getTipoMatricula());

        matriculaRepository.save(matricula);
        return toResponseConApoderados(matricula);
    }

    @Override
    public MatriculaResponse cambiarEstado(Long id, EstadoMatricula nuevoEstado) {
        Matricula matricula = buscarPorId(id);
        matricula.setEstado(nuevoEstado);
        matriculaRepository.save(matricula);
        return toResponseConApoderados(matricula);
    }

    private Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada"));
    }

    private MatriculaResponse toResponseConApoderados(Matricula matricula) {
        List<EstudianteApoderado> apoderados =
                estudianteApoderadoService.listarApoderadosDeEstudiante(matricula.getEstudiante().getId());
        return matriculaMapper.toResponse(matricula, apoderados);
    }
}