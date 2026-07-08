package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.RolRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.RolResponse;
import upc.colegioossbackend01.service.RolService;
import upc.colegioossbackend01.dto.request.AsignarPermisosRequest;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Gestión de roles del sistema (solo ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    @Operation(summary = "Crear rol")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody RolRequest request) {
        RolResponse response = rolService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Rol creado exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar roles", description = "Por defecto solo activos; usar incluirInactivos=true para ver todos")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<RolResponse> response = rolService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de roles"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<ControllerResponse> obtenerPorId(@PathVariable Long id) {
        RolResponse response = rolService.obtenerPorId(id);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Rol encontrado"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar descripción de un rol")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody RolRequest request) {
        RolResponse response = rolService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Rol actualizado exitosamente"));
    }

    @PutMapping("/{id}/permisos")
    @Operation(summary = "Asignar permisos a un rol", description = "Reemplaza el conjunto de permisos del rol con la lista enviada")
    public ResponseEntity<ControllerResponse> asignarPermisos(@PathVariable Long id, @Valid @RequestBody AsignarPermisosRequest request) {
        RolResponse response = rolService.asignarPermisos(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Permisos asignados exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un rol")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        rolService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Rol desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Activar un rol")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        rolService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Rol activado exitosamente"));
    }
}