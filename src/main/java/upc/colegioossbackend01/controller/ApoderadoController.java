package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.ApoderadoRequest;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.service.ApoderadoService;

import java.util.List;

@RestController
@RequestMapping("/api/apoderados")
@Tag(name = "Apoderados", description = "Maestro de apoderados")
public class ApoderadoController {

    private final ApoderadoService apoderadoService;

    public ApoderadoController(ApoderadoService apoderadoService) {
        this.apoderadoService = apoderadoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MATRICULAR')")
    @Operation(summary = "Crear apoderado")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody ApoderadoRequest request) {
        ApoderadoResponse response = apoderadoService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Apoderado creado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Listar apoderados")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<ApoderadoResponse> response = apoderadoService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de apoderados"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Buscar apoderado por número de documento", description = "Usado por el frontend para detectar si el apoderado ya existe antes de matricular")
    public ResponseEntity<ControllerResponse> buscarPorDocumento(@RequestParam String numeroDocumento) {
        return apoderadoService.buscarPorDocumento(numeroDocumento)
                .map(response -> ResponseEntity.ok(ControllerResponse.ok(response, "Apoderado encontrado")))
                .orElseThrow(() -> new ResourceNotFoundException("No existe un apoderado con ese número de documento"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Obtener apoderado por ID")
    public ResponseEntity<ControllerResponse> obtenerPorId(@PathVariable Long id) {
        ApoderadoResponse response = apoderadoService.obtenerPorId(id);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Apoderado encontrado"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Actualizar apoderado")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ApoderadoRequest request) {
        ApoderadoResponse response = apoderadoService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Apoderado actualizado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Desactivar apoderado")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        apoderadoService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Apoderado desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Activar apoderado")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        apoderadoService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Apoderado activado exitosamente"));
    }
}