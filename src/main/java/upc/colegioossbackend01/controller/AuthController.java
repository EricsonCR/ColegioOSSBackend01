package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upc.colegioossbackend01.dto.request.LoginRequest;
import upc.colegioossbackend01.dto.response.AuthResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.service.AuthService;
import upc.colegioossbackend01.dto.request.RefreshTokenRequest;
import upc.colegioossbackend01.dto.request.RegisterRequest;
import upc.colegioossbackend01.dto.request.ForgotPasswordRequest;
import upc.colegioossbackend01.dto.request.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y manejo de sesión")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve access token, refresh token, rol y permisos")
    public ResponseEntity<ControllerResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ControllerResponse.ok(authResponse, "Login exitoso"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Genera un nuevo access token a partir de un refresh token válido")
    public ResponseEntity<ControllerResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refresh(request);
        return ResponseEntity.ok(ControllerResponse.ok(authResponse, "Token renovado exitosamente"));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario como ALUMNO (activo de inmediato) o DOCENTE (queda pendiente de aprobación por un administrador)")
    public ResponseEntity<ControllerResponse> register(@Valid @RequestBody RegisterRequest request) {
        String mensaje = authService.register(request);
        return ResponseEntity.ok(ControllerResponse.ok(null, mensaje));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un enlace de recuperación al correo si está registrado en el sistema")
    public ResponseEntity<ControllerResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String mensaje = authService.forgotPassword(request);
        return ResponseEntity.ok(ControllerResponse.ok(null, mensaje));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Actualiza la contraseña usando un token de recuperación válido")
    public ResponseEntity<ControllerResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String mensaje = authService.resetPassword(request);
        return ResponseEntity.ok(ControllerResponse.ok(null, mensaje));
    }
}