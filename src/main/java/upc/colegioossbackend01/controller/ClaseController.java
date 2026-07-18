package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.ClaseRequest;
import upc.colegioossbackend01.dto.response.ClaseResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.service.ClaseService;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@Tag(name = "Clases", description = "Sesiones de clase dictadas (HU-10)")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Registrar una clase dictada")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody ClaseRequest request) {
        ClaseResponse response = claseService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Clase registrada exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar clases de un curso-aula")
    public ResponseEntity<ControllerResponse> listarPorAulaCurso(@RequestParam Long aulaCursoId) {
        List<ClaseResponse> response = claseService.listarPorAulaCurso(aulaCursoId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Clases del curso"));
    }
}