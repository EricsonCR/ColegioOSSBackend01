package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.AsistenciaRequest;
import upc.colegioossbackend01.dto.response.AsistenciaResponse;
import upc.colegioossbackend01.dto.response.ConsolidadoAsistenciaResponse;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.service.AsistenciaService;

@RestController
@RequestMapping("/api/asistencias")
@Tag(name = "Asistencia", description = "Registro de asistencia diaria (HU-10)")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Registrar o corregir la asistencia de un estudiante")
    public ResponseEntity<ControllerResponse> registrar(@Valid @RequestBody AsistenciaRequest request) {
        AsistenciaResponse response = asistenciaService.registrar(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Asistencia registrada exitosamente"));
    }

    @GetMapping("/consolidado")
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Consolidado de asistencia de una clase")
    public ResponseEntity<ControllerResponse> obtenerConsolidado(@RequestParam Long claseId) {
        ConsolidadoAsistenciaResponse response = asistenciaService.obtenerConsolidado(claseId);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Consolidado de asistencia"));
    }
}