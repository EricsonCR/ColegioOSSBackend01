package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.DocenteCursoRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.DocenteCursoResponse;
import upc.colegioossbackend01.service.DocenteCursoService;

import java.util.List;

@RestController
@RequestMapping("/api/docente-cursos")
@Tag(name = "Docente-Curso", description = "Asignación de docentes a cursos-aula")
public class DocenteCursoController {

    private final DocenteCursoService docenteCursoService;

    public DocenteCursoController(DocenteCursoService docenteCursoService) {
        this.docenteCursoService = docenteCursoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar un docente a un curso-aula")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody DocenteCursoRequest request) {
        DocenteCursoResponse response = docenteCursoService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Docente asignado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @Operation(summary = "Listar cursos-aula de un docente")
    public ResponseEntity<ControllerResponse> listarPorDocente(@RequestParam Long usuarioId) {
        List<DocenteCursoResponse> response = docenteCursoService.listarPorDocente(usuarioId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Cursos del docente"));
    }

    @GetMapping("/mis-cursos")
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Listar mis cursos asignados", description = "Usa el usuario autenticado, sin necesitar pasar el ID")
    public ResponseEntity<ControllerResponse> misCursos(Authentication authentication) {
        List<DocenteCursoResponse> response = docenteCursoService.listarPorDocenteUsername(authentication.getName());
        return ResponseEntity.ok(ControllerResponse.ok(response, "Mis cursos asignados"));
    }

    @GetMapping("/por-aula-curso")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar docentes asignados a un curso-aula")
    public ResponseEntity<ControllerResponse> listarPorAulaCurso(@RequestParam Long aulaCursoId) {
        List<DocenteCursoResponse> response = docenteCursoService.listarPorAulaCurso(aulaCursoId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Docentes del curso-aula"));
    }
}