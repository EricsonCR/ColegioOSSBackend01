package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.NotaRequest;
import upc.colegioossbackend01.dto.response.ConsolidadoNotaResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.NotaResponse;
import upc.colegioossbackend01.service.NotaService;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
@Tag(name = "Notas", description = "Registro de calificaciones (HU-07)")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Registrar o corregir una nota")
    public ResponseEntity<ControllerResponse> registrar(@Valid @RequestBody NotaRequest request) {
        NotaResponse response = notaService.registrar(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Nota registrada exitosamente"));
    }

    @GetMapping("/por-matricula")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar notas de un estudiante (por matrícula)")
    public ResponseEntity<ControllerResponse> listarPorMatricula(@RequestParam Long matriculaId) {
        List<NotaResponse> response = notaService.listarPorMatricula(matriculaId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Notas del estudiante"));
    }

    @GetMapping("/por-componente")
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Listar notas de todos los estudiantes en un componente de evaluación")
    public ResponseEntity<ControllerResponse> listarPorComponente(@RequestParam Long cursoEvaluacionId) {
        List<NotaResponse> response = notaService.listarPorCursoEvaluacion(cursoEvaluacionId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Notas del componente de evaluación"));
    }

    @GetMapping("/consolidado")
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Consolidado de notas", description = "Lista todos los estudiantes matriculados en el aula de un componente de evaluación, con su nota actual o vacía si aún no la tiene")
    public ResponseEntity<ControllerResponse> obtenerConsolidado(@RequestParam Long cursoEvaluacionId) {
        ConsolidadoNotaResponse response = notaService.obtenerConsolidado(cursoEvaluacionId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Consolidado de notas"));
    }
}