package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.MatriculaRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;
import upc.colegioossbackend01.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@Tag(name = "Matrícula - Consulta y edición", description = "Consultar y editar matrículas existentes")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Listar matrículas", description = "Filtros opcionales: periodo, nivel, grado, estado")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) Nivel nivel,
            @RequestParam(required = false) Integer grado,
            @RequestParam(required = false) EstadoMatricula estado) {
        List<MatriculaResponse> response = matriculaService.listar(periodo, nivel, grado, estado);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de matrículas"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Obtener matrícula por ID")
    public ResponseEntity<ControllerResponse> obtenerPorId(@PathVariable Long id) {
        MatriculaResponse response = matriculaService.obtenerPorId(id);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Matrícula encontrada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Actualizar matrícula", description = "No permite cambiar el estudiante ni el periodo")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody MatriculaRequest request) {
        MatriculaResponse response = matriculaService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Matrícula actualizada exitosamente"));
    }

    @PatchMapping("/{id}/retirar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Retirar matrícula", description = "Ej. por expulsión a mitad de año")
    public ResponseEntity<ControllerResponse> retirar(@PathVariable Long id) {
        MatriculaResponse response = matriculaService.cambiarEstado(id, EstadoMatricula.RETIRADA);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Matrícula retirada exitosamente"));
    }

    @PatchMapping("/{id}/trasladar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Marcar matrícula como trasladada")
    public ResponseEntity<ControllerResponse> trasladar(@PathVariable Long id) {
        MatriculaResponse response = matriculaService.cambiarEstado(id, EstadoMatricula.TRASLADADA);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Matrícula marcada como trasladada exitosamente"));
    }
}