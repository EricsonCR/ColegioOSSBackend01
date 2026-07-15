package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.AprobarUsuarioRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.UsuarioResponse;
import upc.colegioossbackend01.service.UsuarioService;
import upc.colegioossbackend01.enums.EstadoUsuario;
import upc.colegioossbackend01.dto.request.CambiarRolRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios (solo ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar usuarios pendientes de aprobación")
    public ResponseEntity<ControllerResponse> listarPendientes() {
        List<UsuarioResponse> response = usuarioService.listarPendientes();
        return ResponseEntity.ok(ControllerResponse.ok(response, "Usuarios pendientes de aprobación"));
    }

    @PatchMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar usuario pendiente", description = "Asigna un rol al usuario y lo activa")
    public ResponseEntity<ControllerResponse> aprobarUsuario(@PathVariable Long id, @Valid @RequestBody AprobarUsuarioRequest request) {
        UsuarioResponse response = usuarioService.aprobarUsuario(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Usuario aprobado y activado exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Filtros opcionales por estado y/o rol")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(required = false) EstadoUsuario estado,
            @RequestParam(required = false) Long rolId) {
        List<UsuarioResponse> response = usuarioService.listar(estado, rolId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de usuarios"));
    }

    @PatchMapping("/{id}/cambiar-rol")
    @Operation(summary = "Cambiar el rol de un usuario activo")
    public ResponseEntity<ControllerResponse> cambiarRol(@PathVariable Long id, @Valid @RequestBody CambiarRolRequest request) {
        UsuarioResponse response = usuarioService.cambiarRol(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Rol actualizado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Activar usuario")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id, Authentication authentication) {
        UsuarioResponse response = usuarioService.cambiarEstado(authentication.getName(), id, EstadoUsuario.ACTIVO);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Usuario activado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar usuario")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id, Authentication authentication) {
        UsuarioResponse response = usuarioService.cambiarEstado(authentication.getName(), id, EstadoUsuario.INACTIVO);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Usuario desactivado exitosamente"));
    }

    @PatchMapping("/{id}/bloquear")
    @Operation(summary = "Bloquear usuario")
    public ResponseEntity<ControllerResponse> bloquear(@PathVariable Long id, Authentication authentication) {
        UsuarioResponse response = usuarioService.cambiarEstado(authentication.getName(), id, EstadoUsuario.BLOQUEADO);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Usuario bloqueado exitosamente"));
    }
}