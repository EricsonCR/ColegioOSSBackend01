package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.PermisoRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.PermisoResponse;
import upc.colegioossbackend01.service.PermisoService;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Gestión de permisos del sistema (solo ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @PostMapping
    @Operation(summary = "Crear permiso")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody PermisoRequest request) {
        PermisoResponse response = permisoService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Permiso creado exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar permisos", description = "Por defecto solo activos; usar incluirInactivos=true para ver todos")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<PermisoResponse> response = permisoService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de permisos"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener permiso por ID")
    public ResponseEntity<ControllerResponse> obtenerPorId(@PathVariable Long id) {
        PermisoResponse response = permisoService.obtenerPorId(id);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Permiso encontrado"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar descripción de un permiso")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody PermisoRequest request) {
        PermisoResponse response = permisoService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Permiso actualizado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un permiso")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        permisoService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Permiso desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Activar un permiso")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        permisoService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Permiso activado exitosamente"));
    }
}