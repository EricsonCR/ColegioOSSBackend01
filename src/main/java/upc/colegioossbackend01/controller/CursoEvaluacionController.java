package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.CursoEvaluacionRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.CursoEvaluacionResponse;
import upc.colegioossbackend01.service.CursoEvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/curso-evaluaciones")
@Tag(name = "Curso-Evaluacion", description = "Componentes de evaluación por curso, aula y bimestre")
public class CursoEvaluacionController {

    private final CursoEvaluacionService cursoEvaluacionService;

    public CursoEvaluacionController(CursoEvaluacionService cursoEvaluacionService) {
        this.cursoEvaluacionService = cursoEvaluacionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Crear un componente de evaluación")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody CursoEvaluacionRequest request) {
        CursoEvaluacionResponse response = cursoEvaluacionService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Componente de evaluación creado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar componentes de evaluación de un curso-aula y bimestre")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam Long aulaCursoId,
            @RequestParam Integer bimestre) {
        List<CursoEvaluacionResponse> response = cursoEvaluacionService.listar(aulaCursoId, bimestre);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Componentes de evaluación"));
    }

    @GetMapping("/por-aula-curso")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar todos los componentes de evaluación de un curso-aula (todos los bimestres)")
    public ResponseEntity<ControllerResponse> listarPorAulaCurso(@RequestParam Long aulaCursoId) {
        List<CursoEvaluacionResponse> response = cursoEvaluacionService.listarPorAulaCurso(aulaCursoId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Componentes de evaluación del curso-aula"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Editar un componente de evaluación")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CursoEvaluacionRequest request) {
        CursoEvaluacionResponse response = cursoEvaluacionService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Componente de evaluación actualizado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Desactivar un componente de evaluación")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        cursoEvaluacionService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Componente desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Activar un componente de evaluación")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        cursoEvaluacionService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Componente activado exitosamente"));
    }
}