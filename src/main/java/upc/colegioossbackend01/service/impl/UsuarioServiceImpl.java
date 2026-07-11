package upc.colegioossbackend01.service.impl;

import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.AprobarUsuarioRequest;
import upc.colegioossbackend01.dto.response.UsuarioResponse;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.UsuarioMapper;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.repository.UsuarioRepository;
import upc.colegioossbackend01.service.UsuarioService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public List<UsuarioResponse> listarPendientes() {
        return usuarioRepository.findByEstado(EstadoUsuario.PENDIENTE)
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse aprobarUsuario(Long usuarioId, AprobarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (usuario.getEstado() != EstadoUsuario.PENDIENTE) {
            throw new BusinessException("El usuario no está en estado pendiente de aprobación");
        }

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        if (!rol.isActivo()) {
            throw new BusinessException("El rol seleccionado se encuentra inactivo");
        }

        if ("ADMIN".equals(rol.getNombre())) {
            throw new BusinessException("No se puede asignar el rol ADMIN desde este endpoint");
        }

        usuario.setRol(rol);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioResponse(usuario);
    }
}