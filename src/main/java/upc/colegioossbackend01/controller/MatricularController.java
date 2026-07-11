package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upc.colegioossbackend01.dto.request.MatricularRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.MatriculaResponse;
import upc.colegioossbackend01.service.MatricularService;

@RestController
@RequestMapping("/api/matriculas")
@Tag(name = "Matrícula", description = "Registro de matrícula de estudiantes")
public class MatricularController {

    private final MatricularService matricularService;

    public MatricularController(MatricularService matricularService) {
        this.matricularService = matricularService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MATRICULAR')")
    @Operation(summary = "Matricular estudiante", description = "Registra la matrícula de un estudiante nuevo o existente, junto con sus apoderados")
    public ResponseEntity<ControllerResponse> matricular(@Valid @RequestBody MatricularRequest request) {
        MatriculaResponse response = matricularService.matricular(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Matrícula registrada exitosamente"));
    }
}