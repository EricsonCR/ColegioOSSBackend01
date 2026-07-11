package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.EstudianteRequest;
import upc.colegioossbackend01.dto.response.EstudianteResponse;
import upc.colegioossbackend01.entity.Estudiante;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.EstudianteMapper;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.service.EstudianteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteMapper estudianteMapper;

    public EstudianteServiceImpl(EstudianteRepository estudianteRepository, EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteMapper = estudianteMapper;
    }

    @Override
    public EstudianteResponse crear(EstudianteRequest request) {
        if (estudianteRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BusinessException("Ya existe un estudiante con ese número de documento");
        }

        Estudiante estudiante = Estudiante.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .direccion(request.getDireccion())
                .activo(true)
                .build();

        estudianteRepository.save(estudiante);
        return estudianteMapper.toResponse(estudiante);
    }

    @Override
    public Optional<EstudianteResponse> buscarPorDocumento(String numeroDocumento) {
        return estudianteRepository.findByNumeroDocumento(numeroDocumento)
                .map(estudianteMapper::toResponse);
    }

    @Override
    public List<EstudianteResponse> listar(boolean incluirInactivos) {
        List<Estudiante> estudiantes = incluirInactivos
                ? estudianteRepository.findAll()
                : estudianteRepository.findByActivoTrue();

        return estudiantes.stream()
                .map(estudianteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteResponse obtenerPorId(Long id) {
        Estudiante estudiante = buscarPorId(id);
        return estudianteMapper.toResponse(estudiante);
    }

    @Override
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = buscarPorId(id);

        if (!estudiante.getNumeroDocumento().equals(request.getNumeroDocumento())) {
            boolean existeEnOtro = estudianteRepository.findByNumeroDocumento(request.getNumeroDocumento())
                    .filter(e -> !e.getId().equals(id))
                    .isPresent();

            if (existeEnOtro) {
                throw new BusinessException("Ya existe otro estudiante con ese número de documento");
            }
        }

        estudiante.setTipoDocumento(request.getTipoDocumento());
        estudiante.setNumeroDocumento(request.getNumeroDocumento());
        estudiante.setNombres(request.getNombres());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        estudiante.setGenero(request.getGenero());
        estudiante.setDireccion(request.getDireccion());

        estudianteRepository.save(estudiante);
        return estudianteMapper.toResponse(estudiante);
    }

    @Override
    public void desactivar(Long id) {
        Estudiante estudiante = buscarPorId(id);
        estudiante.setActivo(false);
        estudianteRepository.save(estudiante);
    }

    @Override
    public void activar(Long id) {
        Estudiante estudiante = buscarPorId(id);
        estudiante.setActivo(true);
        estudianteRepository.save(estudiante);
    }

    private Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    }
}