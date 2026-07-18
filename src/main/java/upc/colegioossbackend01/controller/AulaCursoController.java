package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.AulaCursoRequest;
import upc.colegioossbackend01.dto.response.AulaCursoResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.service.AulaCursoService;

import java.util.List;

@RestController
@RequestMapping("/api/aula-cursos")
@Tag(name = "Aula-Curso", description = "Cursos dictados en cada aula")
public class AulaCursoController {

    private final AulaCursoService aulaCursoService;

    public AulaCursoController(AulaCursoService aulaCursoService) {
        this.aulaCursoService = aulaCursoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar un curso a un aula")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody AulaCursoRequest request) {
        AulaCursoResponse response = aulaCursoService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Curso asignado al aula exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar cursos de un aula")
    public ResponseEntity<ControllerResponse> listarPorAula(@RequestParam Long aulaId) {
        List<AulaCursoResponse> response = aulaCursoService.listarPorAula(aulaId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Cursos del aula"));
    }
}