package upc.colegioossbackend01.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import upc.colegioossbackend01.dto.request.LoginRequest;
import upc.colegioossbackend01.dto.response.AuthResponse;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.UsuarioMapper;
import upc.colegioossbackend01.repository.UsuarioRepository;
import upc.colegioossbackend01.security.JwtService;
import upc.colegioossbackend01.service.AuthService;
import upc.colegioossbackend01.dto.request.RefreshTokenRequest;
import upc.colegioossbackend01.exception.InvalidTokenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import upc.colegioossbackend01.dto.request.RegisterRequest;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.repository.RolRepository;

import upc.colegioossbackend01.dto.request.ForgotPasswordRequest;
import upc.colegioossbackend01.dto.request.ResetPasswordRequest;
import upc.colegioossbackend01.entity.PasswordResetToken;
import upc.colegioossbackend01.repository.PasswordResetTokenRepository;
import upc.colegioossbackend01.service.EmailService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           JwtService jwtService,
                           UsuarioMapper usuarioMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsernameWithRolYPermisos(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String accessToken = jwtService.generateAccessToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return usuarioMapper.toAuthResponse(usuario, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new InvalidTokenException("El token proporcionado no es un refresh token válido");
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException("El refresh token ha expirado, inicie sesión nuevamente");
        }

        String username = jwtService.extractUsername(refreshToken);

        Usuario usuario = usuarioRepository.findByUsernameWithRolYPermisos(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateAccessToken(usuario);

        return usuarioMapper.toAuthResponse(usuario, newAccessToken, refreshToken);
    }

    @Override
    public String register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El usuario ya existe");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());

        if ("ALUMNO".equals(request.getRolSolicitado())) {
            Rol rolAlumno = rolRepository.findByNombre("ALUMNO")
                    .orElseThrow(() -> new ResourceNotFoundException("Rol ALUMNO no configurado en el sistema"));

            usuario.setRol(rolAlumno);
            usuario.setEstado(EstadoUsuario.ACTIVO);
            usuarioRepository.save(usuario);

            return "Usuario registrado. Su cuenta ya está activa, ya puede iniciar sesión.";
        } else {
            usuario.setRol(null);
            usuario.setEstado(EstadoUsuario.PENDIENTE);
            usuarioRepository.save(usuario);

            return "Usuario registrado. Su cuenta está pendiente de aprobación por un administrador.";
        }
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        String mensajeGenerico = "Si el correo está registrado, se ha enviado un enlace de recuperación";

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        if (usuario == null) {
            return mensajeGenerico;
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(30))
                .usado(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        boolean enviado = emailService.enviarCorreoRecuperacion(usuario.getEmail(), token);
        if (!enviado) {
            System.out.println("ADVERTENCIA: no se pudo enviar el correo de recuperación a " + usuario.getEmail());
        }

        return mensajeGenerico;
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Token de recuperación inválido"));

        if (resetToken.isUsado()) {
            throw new InvalidTokenException("Este token ya fue utilizado");
        }

        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El token ha expirado, solicite uno nuevo");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        passwordResetTokenRepository.save(resetToken);

        return "Contraseña actualizada correctamente";
    }
}