package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.PermisoRequest;
import upc.colegioossbackend01.dto.response.PermisoResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.PermisoMapper;
import upc.colegioossbackend01.repository.PermisoRepository;
import upc.colegioossbackend01.service.PermisoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;
    private final PermisoMapper permisoMapper;

    public PermisoServiceImpl(PermisoRepository permisoRepository, PermisoMapper permisoMapper) {
        this.permisoRepository = permisoRepository;
        this.permisoMapper = permisoMapper;
    }

    @Override
    public PermisoResponse crear(PermisoRequest request) {
        if (permisoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un permiso con ese código");
        }

        Permiso permiso = Permiso.builder()
                .codigo(request.getCodigo())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();

        permisoRepository.save(permiso);
        return permisoMapper.toResponse(permiso);
    }

    @Override
    public List<PermisoResponse> listar(boolean incluirInactivos) {
        List<Permiso> permisos = incluirInactivos
                ? permisoRepository.findAll()
                : permisoRepository.findByActivoTrue();

        return permisos.stream()
                .map(permisoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PermisoResponse obtenerPorId(Long id) {
        Permiso permiso = buscarPorId(id);
        return permisoMapper.toResponse(permiso);
    }

    @Override
    public PermisoResponse actualizar(Long id, PermisoRequest request) {
        Permiso permiso = buscarPorId(id);

        if (!permiso.getCodigo().equals(request.getCodigo())) {
            throw new BusinessException("El código de un permiso no puede modificarse");
        }

        permiso.setDescripcion(request.getDescripcion());
        permisoRepository.save(permiso);

        return permisoMapper.toResponse(permiso);
    }

    @Override
    public void desactivar(Long id) {
        Permiso permiso = buscarPorId(id);
        permiso.setActivo(false);
        permisoRepository.save(permiso);
    }

    @Override
    public void activar(Long id) {
        Permiso permiso = buscarPorId(id);
        permiso.setActivo(true);
        permisoRepository.save(permiso);
    }

    private Permiso buscarPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado"));
    }
}