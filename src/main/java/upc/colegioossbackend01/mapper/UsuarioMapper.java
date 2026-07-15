package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.AuthResponse;
import upc.colegioossbackend01.dto.response.UsuarioResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.entity.Usuario;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public AuthResponse toAuthResponse(Usuario usuario, String token, String refreshToken) {
        List<String> permisos = usuario.getRol().getPermisos()
                .stream()
                .map(Permiso::getCodigo)
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .rol(usuario.getRol().getNombre())
                .nombreCompleto(usuario.getNombreCompleto())
                .permisos(permisos)
                .build();
    }

    public UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .estado(usuario.getEstado())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombre() : null)
                .rolSolicitado(usuario.getRolSolicitado())
                .build();
    }
}