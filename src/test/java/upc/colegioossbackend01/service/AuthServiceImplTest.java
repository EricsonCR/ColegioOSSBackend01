package upc.colegioossbackend01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import upc.colegioossbackend01.dto.request.LoginRequest;
import upc.colegioossbackend01.dto.request.RefreshTokenRequest;
import upc.colegioossbackend01.dto.request.RegisterRequest;
import upc.colegioossbackend01.dto.response.AuthResponse;
import upc.colegioossbackend01.entity.Permiso;
import upc.colegioossbackend01.entity.Rol;
import upc.colegioossbackend01.entity.Usuario;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.exception.BusinessException;
import upc.colegioossbackend01.exception.InvalidTokenException;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.mapper.UsuarioMapper;
import upc.colegioossbackend01.repository.RolRepository;
import upc.colegioossbackend01.repository.UsuarioRepository;
import upc.colegioossbackend01.security.JwtService;
import upc.colegioossbackend01.service.impl.AuthServiceImpl;
import upc.colegioossbackend01.dto.request.ForgotPasswordRequest;
import upc.colegioossbackend01.dto.request.ResetPasswordRequest;
import upc.colegioossbackend01.entity.PasswordResetToken;
import upc.colegioossbackend01.repository.PasswordResetTokenRepository;
import upc.colegioossbackend01.repository.EstudianteRepository;
import upc.colegioossbackend01.repository.ApoderadoRepository;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private ApoderadoRepository apoderadoRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioActivo;
    private Rol rolEstudiante;

    @BeforeEach
    void setUp() {
        Permiso permiso = Permiso.builder().id(1L).codigo("USUARIO_VER").build();

        rolEstudiante = Rol.builder()
                .id(1L)
                .nombre("ESTUDIANTE")
                .permisos(Set.of(permiso))
                .build();

        usuarioActivo = Usuario.builder()
                .id(1L)
                .username("juan")
                .password("hashed_password")
                .estado(EstadoUsuario.ACTIVO)
                .rol(rolEstudiante)
                .build();
    }

    // ---------- LOGIN ----------

    @Test
    void login_deberiaRetornarAuthResponse_cuandoCredencialesValidas() {
        LoginRequest request = LoginRequest.builder().username("juan").password("123456").build();

        when(usuarioRepository.findByUsernameWithRolYPermisos("juan")).thenReturn(Optional.of(usuarioActivo));
        when(jwtService.generateAccessToken(usuarioActivo)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(usuarioActivo)).thenReturn("refresh-token");
        when(usuarioMapper.toAuthResponse(usuarioActivo, "access-token", "refresh-token"))
                .thenReturn(AuthResponse.builder().token("access-token").refreshToken("refresh-token").build());

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_deberiaLanzarBadCredentials_cuandoAuthenticationManagerFalla() {
        LoginRequest request = LoginRequest.builder().username("juan").password("incorrecta").build();

        doThrow(new BadCredentialsException("Credenciales inválidas"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }

    @Test
    void login_deberiaLanzarResourceNotFound_cuandoUsuarioNoExisteDespuesDeAutenticar() {
        LoginRequest request = LoginRequest.builder().username("fantasma").password("123456").build();

        when(usuarioRepository.findByUsernameWithRolYPermisos("fantasma")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- REFRESH ----------

    @Test
    void refresh_deberiaRetornarNuevoAccessToken_cuandoRefreshTokenValido() {
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken("valid-refresh").build();

        when(jwtService.extractTokenType("valid-refresh")).thenReturn("refresh");
        when(jwtService.isTokenExpired("valid-refresh")).thenReturn(false);
        when(jwtService.extractUsername("valid-refresh")).thenReturn("juan");
        when(usuarioRepository.findByUsernameWithRolYPermisos("juan")).thenReturn(Optional.of(usuarioActivo));
        when(jwtService.generateAccessToken(usuarioActivo)).thenReturn("nuevo-access-token");
        when(usuarioMapper.toAuthResponse(usuarioActivo, "nuevo-access-token", "valid-refresh"))
                .thenReturn(AuthResponse.builder().token("nuevo-access-token").refreshToken("valid-refresh").build());

        AuthResponse response = authService.refresh(request);

        assertThat(response.getToken()).isEqualTo("nuevo-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("valid-refresh");
    }

    @Test
    void refresh_deberiaLanzarInvalidToken_cuandoTipoNoEsRefresh() {
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken("access-token-usado-mal").build();

        when(jwtService.extractTokenType("access-token-usado-mal")).thenReturn("access");

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("no es un refresh token válido");
    }

    @Test
    void refresh_deberiaLanzarInvalidToken_cuandoTokenExpirado() {
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken("expired-refresh").build();

        when(jwtService.extractTokenType("expired-refresh")).thenReturn("refresh");
        when(jwtService.isTokenExpired("expired-refresh")).thenReturn(true);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expirado");
    }

    // ---------- REGISTER ----------

    @Test
    void register_deberiaActivarInmediatamente_cuandoRolSolicitadoEsEstudiante() {
        RegisterRequest request = RegisterRequest.builder()
                .username("nuevo_estudiante")
                .password("123456")
                .nombreCompleto("Nuevo Estudiante")
                .rolSolicitado("ESTUDIANTE")
                .build();

        when(usuarioRepository.existsByUsername("nuevo_estudiante")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(rolRepository.findByNombre("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
        when(rolRepository.findByNombre("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
        // no hace falta stubear estudianteRepository.save si el mock ya no es null

        String mensaje = authService.register(request);

        assertThat(mensaje).containsIgnoringCase("activa");
        verify(usuarioRepository).save(argThat(u ->
                u.getEstado() == EstadoUsuario.ACTIVO && u.getRol() == rolEstudiante));
    }

    @Test
    void register_deberiaQuedarPendiente_cuandoRolSolicitadoEsDocente() {
        RegisterRequest request = RegisterRequest.builder()
                .username("nuevo_docente")
                .password("123456")
                .nombreCompleto("Nuevo Docente")
                .rolSolicitado("DOCENTE")
                .build();

        when(usuarioRepository.existsByUsername("nuevo_docente")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");

        String mensaje = authService.register(request);

        assertThat(mensaje).containsIgnoringCase("pendiente");
        verify(usuarioRepository).save(argThat(u ->
                u.getEstado() == EstadoUsuario.PENDIENTE && u.getRol() == null));
        verifyNoInteractions(rolRepository);
    }

    @Test
    void register_deberiaLanzarBusinessException_cuandoUsernameYaExiste() {
        RegisterRequest request = RegisterRequest.builder()
                .username("juan")
                .password("123456")
                .nombreCompleto("Juan Duplicado")
                .rolSolicitado("ESTUDIANTE")
                .build();

        when(usuarioRepository.existsByUsername("juan")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya existe");

        verify(usuarioRepository, never()).save(any());
    }

    // ---------- FORGOT PASSWORD ----------

    @Test
    void forgotPassword_deberiaGenerarTokenYEnviarCorreo_cuandoUsuarioExiste() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder().email("juan@correo.com").build();

        usuarioActivo.setEmail("juan@correo.com");
        when(usuarioRepository.findByEmail("juan@correo.com")).thenReturn(Optional.of(usuarioActivo));
        when(emailService.enviarCorreoRecuperacion(eq("juan@correo.com"), anyString())).thenReturn(true);

        String mensaje = authService.forgotPassword(request);

        assertThat(mensaje).containsIgnoringCase("enviado");
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).enviarCorreoRecuperacion(eq("juan@correo.com"), anyString());
    }

    @Test
    void forgotPassword_deberiaRetornarMensajeGenerico_cuandoEmailNoExiste() {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder().email("noexiste@correo.com").build();

        when(usuarioRepository.findByEmail("noexiste@correo.com")).thenReturn(Optional.empty());

        String mensaje = authService.forgotPassword(request);

        assertThat(mensaje).containsIgnoringCase("enviado");
        verifyNoInteractions(passwordResetTokenRepository);
        verifyNoInteractions(emailService);
    }

// ---------- RESET PASSWORD ----------

    @Test
    void resetPassword_deberiaActualizarPassword_cuandoTokenValido() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-token")
                .nuevaPassword("NuevaClave123!")
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("valid-token")
                .usuario(usuarioActivo)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(10))
                .usado(false)
                .build();

        when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("NuevaClave123!")).thenReturn("hashed-nueva-clave");

        String mensaje = authService.resetPassword(request);

        assertThat(mensaje).containsIgnoringCase("actualizada");
        verify(usuarioRepository).save(argThat(u -> "hashed-nueva-clave".equals(u.getPassword())));
        verify(passwordResetTokenRepository).save(argThat(PasswordResetToken::isUsado));
    }

    @Test
    void resetPassword_deberiaLanzarInvalidToken_cuandoTokenNoExiste() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("token-inexistente")
                .nuevaPassword("NuevaClave123!")
                .build();

        when(passwordResetTokenRepository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void resetPassword_deberiaLanzarInvalidToken_cuandoTokenYaFueUsado() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("token-usado")
                .nuevaPassword("NuevaClave123!")
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("token-usado")
                .usuario(usuarioActivo)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(10))
                .usado(true)
                .build();

        when(passwordResetTokenRepository.findByToken("token-usado")).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("ya fue utilizado");
    }

    @Test
    void resetPassword_deberiaLanzarInvalidToken_cuandoTokenExpirado() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("token-expirado")
                .nuevaPassword("NuevaClave123!")
                .build();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("token-expirado")
                .usuario(usuarioActivo)
                .fechaExpiracion(LocalDateTime.now().minusMinutes(5))
                .usado(false)
                .build();

        when(passwordResetTokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expirado");
    }
}