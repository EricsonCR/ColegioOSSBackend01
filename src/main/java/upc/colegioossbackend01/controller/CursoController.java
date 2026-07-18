package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.CursoRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.CursoResponse;
import upc.colegioossbackend01.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Catálogo de cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear curso")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody CursoRequest request) {
        CursoResponse response = cursoService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Curso creado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar cursos")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<CursoResponse> response = cursoService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de cursos"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar curso")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
        CursoResponse response = cursoService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Curso actualizado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar curso")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        cursoService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Curso desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar curso")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        cursoService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Curso activado exitosamente"));
    }
}