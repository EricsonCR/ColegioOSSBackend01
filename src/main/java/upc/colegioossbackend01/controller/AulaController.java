package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.AulaRequest;
import upc.colegioossbackend01.dto.response.AulaResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.service.AulaService;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "Aulas", description = "Gestión de aulas por periodo, nivel, grado y sección")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear aula")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody AulaRequest request) {
        AulaResponse response = aulaService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Aula creada exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar aulas")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<AulaResponse> response = aulaService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de aulas"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar aula")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody AulaRequest request) {
        AulaResponse response = aulaService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Aula actualizada exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar aula")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        aulaService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Aula desactivada exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar aula")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        aulaService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Aula activada exitosamente"));
    }
}