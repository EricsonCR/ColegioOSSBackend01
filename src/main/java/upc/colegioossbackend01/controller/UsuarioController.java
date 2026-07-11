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
}