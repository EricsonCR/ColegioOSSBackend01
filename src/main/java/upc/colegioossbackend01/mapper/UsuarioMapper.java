package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.AuthResponse;
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
                .permisos(permisos)
                .build();
    }
}