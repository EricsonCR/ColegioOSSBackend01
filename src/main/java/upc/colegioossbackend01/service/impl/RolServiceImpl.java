package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.RolRequest;
import upc.colegioossbackend01.dto.response.RolResponse;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.RolMapper;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.service.RolService;

import upc.colegioossbackend01.dto.request.AsignarPermisosRequest;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.repository.PermisoRepository;

import java.util.HashSet;
import java.util.Set;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(RolRepository rolRepository, PermisoRepository permisoRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.rolMapper = rolMapper;
    }
    @Override
    public RolResponse crear(RolRequest request) {
        if (rolRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un rol con ese nombre");
        }

        Rol rol = Rol.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();

        rolRepository.save(rol);
        return rolMapper.toResponse(rol);
    }

    @Override
    public List<RolResponse> listar(boolean incluirInactivos) {
        List<Rol> roles = incluirInactivos
                ? rolRepository.findAll()
                : rolRepository.findByActivoTrue();

        return roles.stream()
                .map(rolMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RolResponse obtenerPorId(Long id) {
        Rol rol = buscarPorId(id);
        return rolMapper.toResponse(rol);
    }

    @Override
    public RolResponse actualizar(Long id, RolRequest request) {
        Rol rol = buscarPorId(id);

        if (!rol.getNombre().equals(request.getNombre())) {
            throw new BusinessException("El nombre de un rol no puede modificarse");
        }

        rol.setDescripcion(request.getDescripcion());
        rolRepository.save(rol);

        return rolMapper.toResponse(rol);
    }

    @Override
    public void desactivar(Long id) {
        Rol rol = buscarPorId(id);
        rol.setActivo(false);
        rolRepository.save(rol);
    }

    @Override
    public void activar(Long id) {
        Rol rol = buscarPorId(id);
        rol.setActivo(true);
        rolRepository.save(rol);
    }

    private Rol buscarPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
    }

    @Override
    public RolResponse asignarPermisos(Long rolId, AsignarPermisosRequest request) {
        Rol rol = buscarPorId(rolId);

        Set<Permiso> permisosValidos = new HashSet<>();

        for (Long permisoId : request.getPermisoIds()) {
            Permiso permiso = permisoRepository.findById(permisoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado: id " + permisoId));

            if (!permiso.isActivo()) {
                throw new BusinessException("El permiso '" + permiso.getCodigo() + "' está inactivo y no puede asignarse");
            }

            permisosValidos.add(permiso);
        }

        rol.setPermisos(permisosValidos);
        rolRepository.save(rol);

        return rolMapper.toResponse(rol);
    }
}