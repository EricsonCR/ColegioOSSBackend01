package upc.colegioossbackend01.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import io.jsonwebtoken.JwtException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ControllerResponse> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ControllerResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ControllerResponse> handleBadCredentials(BadCredentialsException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ControllerResponse.error("Usuario o contraseña incorrectos"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ControllerResponse> handleDisabled(DisabledException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ControllerResponse.error("El usuario se encuentra inactivo"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ControllerResponse> handleLocked(LockedException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ControllerResponse.error("El usuario se encuentra bloqueado"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ControllerResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ControllerResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ControllerResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ControllerResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ControllerResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ControllerResponse.error(message));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ControllerResponse> handleJwtException(JwtException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ControllerResponse.error("Token inválido o mal formado"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ControllerResponse> handleAccessDenied(AccessDeniedException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ControllerResponse.error("No tiene permisos para realizar esta acción"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ControllerResponse> handleGeneral(Exception ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ControllerResponse.error("Ocurrió un error inesperado"));
    }
}