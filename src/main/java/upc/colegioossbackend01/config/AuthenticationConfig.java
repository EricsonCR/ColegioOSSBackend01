package upc.colegioossbackend01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AuthenticationConfig {

    private final UsuarioRepository usuarioRepository;

    public AuthenticationConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByUsernameWithRolYPermisos(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            List<GrantedAuthority> authorities;
            if (usuario.getRol() != null) {
                authorities = Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())),
                        usuario.getRol().getPermisos().stream()
                                .map(permiso -> new SimpleGrantedAuthority(permiso.getCodigo()))
                ).collect(Collectors.toList());
            } else {
                authorities = List.of();
            }

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(authorities)
                    .disabled(usuario.getEstado() != EstadoUsuario.ACTIVO)
                    .accountLocked(usuario.getEstado() == EstadoUsuario.BLOQUEADO)
                    .build();
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}