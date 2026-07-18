package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.AulaRequest;
import upc.colegioossbackend01.dto.response.AulaResponse;
import upc.colegioossbackend01.entity.Aula;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.AulaMapper;
import upc.colegioossbackend01.repository.AulaRepository;
import upc.colegioossbackend01.service.AulaService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AulaServiceImpl implements AulaService {

    private final AulaRepository aulaRepository;
    private final AulaMapper aulaMapper;

    public AulaServiceImpl(AulaRepository aulaRepository, AulaMapper aulaMapper) {
        this.aulaRepository = aulaRepository;
        this.aulaMapper = aulaMapper;
    }

    @Override
    public AulaResponse crear(AulaRequest request) {
        boolean existe = aulaRepository.existsByPeriodoAndNivelAndGradoAndSeccion(
                request.getPeriodo(), request.getNivel(), request.getGrado(), request.getSeccion());

        if (existe) {
            throw new BusinessException("Ya existe una aula con ese periodo, nivel, grado y sección");
        }

        Aula aula = Aula.builder()
                .periodo(request.getPeriodo())
                .nivel(request.getNivel())
                .grado(request.getGrado())
                .seccion(request.getSeccion())
                .activo(true)
                .build();

        aulaRepository.save(aula);
        return aulaMapper.toResponse(aula);
    }

    @Override
    public List<AulaResponse> listar(boolean incluirInactivos) {
        List<Aula> aulas = incluirInactivos
                ? aulaRepository.findAll()
                : aulaRepository.findByActivoTrue();

        return aulas.stream()
                .map(aulaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AulaResponse actualizar(Long id, AulaRequest request) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));

        boolean cambioClave = !aula.getPeriodo().equals(request.getPeriodo())
                || aula.getNivel() != request.getNivel()
                || !aula.getGrado().equals(request.getGrado())
                || !aula.getSeccion().equals(request.getSeccion());

        if (cambioClave) {
            boolean existeEnOtra = aulaRepository.findByPeriodoAndNivelAndGradoAndSeccion(
                            request.getPeriodo(), request.getNivel(), request.getGrado(), request.getSeccion())
                    .filter(a -> !a.getId().equals(id))
                    .isPresent();

            if (existeEnOtra) {
                throw new BusinessException("Ya existe otra aula con ese periodo, nivel, grado y sección");
            }
        }

        aula.setPeriodo(request.getPeriodo());
        aula.setNivel(request.getNivel());
        aula.setGrado(request.getGrado());
        aula.setSeccion(request.getSeccion());

        aulaRepository.save(aula);
        return aulaMapper.toResponse(aula);
    }

    @Override
    public void desactivar(Long id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));
        aula.setActivo(false);
        aulaRepository.save(aula);
    }

    @Override
    public void activar(Long id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));
        aula.setActivo(true);
        aulaRepository.save(aula);
    }
}