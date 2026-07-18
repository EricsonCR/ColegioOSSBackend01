package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.EvaluacionRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.EvaluacionResponse;
import upc.colegioossbackend01.service.EvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@Tag(name = "Evaluaciones", description = "Catálogo de tipos de evaluación")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear evaluación")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody EvaluacionRequest request) {
        EvaluacionResponse response = evaluacionService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Evaluación creada exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar evaluaciones")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<EvaluacionResponse> response = evaluacionService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de evaluaciones"));
    }
}