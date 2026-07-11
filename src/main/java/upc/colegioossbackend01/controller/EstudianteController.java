package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import upc.colegioossbackend01.dto.request.EstudianteRequest;
import upc.colegioossbackend01.dto.response.ControllerResponse;
import upc.colegioossbackend01.dto.response.EstudianteResponse;
import upc.colegioossbackend01.exception.ResourceNotFoundException;
import upc.colegioossbackend01.service.EstudianteService;
import upc.colegioossbackend01.dto.request.AsignarApoderadoRequest;
import upc.colegioossbackend01.dto.response.ApoderadoAsignadoResponse;
import upc.colegioossbackend01.entity.EstudianteApoderado;
import upc.colegioossbackend01.mapper.ApoderadoMapper;
import upc.colegioossbackend01.service.EstudianteApoderadoService;

import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "Maestro de estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final EstudianteApoderadoService estudianteApoderadoService;
    private final ApoderadoMapper apoderadoMapper;

    public EstudianteController(EstudianteService estudianteService,
                                EstudianteApoderadoService estudianteApoderadoService,
                                ApoderadoMapper apoderadoMapper) {
        this.estudianteService = estudianteService;
        this.estudianteApoderadoService = estudianteApoderadoService;
        this.apoderadoMapper = apoderadoMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MATRICULAR')")
    @Operation(summary = "Crear estudiante")
    public ResponseEntity<ControllerResponse> crear(@Valid @RequestBody EstudianteRequest request) {
        EstudianteResponse response = estudianteService.crear(request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Estudiante creado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Listar estudiantes")
    public ResponseEntity<ControllerResponse> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        List<EstudianteResponse> response = estudianteService.listar(incluirInactivos);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Listado de estudiantes"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Buscar estudiante por número de documento", description = "Usado por el frontend para detectar si el estudiante ya existe antes de matricular")
    public ResponseEntity<ControllerResponse> buscarPorDocumento(@RequestParam String numeroDocumento) {
        return estudianteService.buscarPorDocumento(numeroDocumento)
                .map(response -> ResponseEntity.ok(ControllerResponse.ok(response, "Estudiante encontrado")))
                .orElseThrow(() -> new ResourceNotFoundException("No existe un estudiante con ese número de documento"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Obtener estudiante por ID")
    public ResponseEntity<ControllerResponse> obtenerPorId(@PathVariable Long id) {
        EstudianteResponse response = estudianteService.obtenerPorId(id);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Estudiante encontrado"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Actualizar estudiante")
    public ResponseEntity<ControllerResponse> actualizar(@PathVariable Long id, @Valid @RequestBody EstudianteRequest request) {
        EstudianteResponse response = estudianteService.actualizar(id, request);
        return ResponseEntity.ok(ControllerResponse.ok(response, "Estudiante actualizado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Desactivar estudiante")
    public ResponseEntity<ControllerResponse> desactivar(@PathVariable Long id) {
        estudianteService.desactivar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Estudiante desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Activar estudiante")
    public ResponseEntity<ControllerResponse> activar(@PathVariable Long id) {
        estudianteService.activar(id);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Estudiante activado exitosamente"));
    }

    @PostMapping("/{id}/apoderados")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Asignar apoderado a un estudiante")
    public ResponseEntity<ControllerResponse> asignarApoderado(@PathVariable Long id, @Valid @RequestBody AsignarApoderadoRequest request) {
        EstudianteApoderado relacion = estudianteApoderadoService.asignarApoderado(id, request);

        ApoderadoAsignadoResponse response = ApoderadoAsignadoResponse.builder()
                .apoderado(apoderadoMapper.toResponse(relacion.getApoderado()))
                .parentesco(relacion.getParentesco())
                .esPrincipal(relacion.isEsPrincipal())
                .build();

        return ResponseEntity.ok(ControllerResponse.ok(response, "Apoderado asignado exitosamente"));
    }

    @GetMapping("/{id}/apoderados")
    @PreAuthorize("hasAuthority('MATRICULA_VER')")
    @Operation(summary = "Listar apoderados de un estudiante")
    public ResponseEntity<ControllerResponse> listarApoderados(@PathVariable Long id) {
        List<ApoderadoAsignadoResponse> response = estudianteApoderadoService.listarApoderadosDeEstudiante(id)
                .stream()
                .map(ea -> ApoderadoAsignadoResponse.builder()
                        .apoderado(apoderadoMapper.toResponse(ea.getApoderado()))
                        .parentesco(ea.getParentesco())
                        .esPrincipal(ea.isEsPrincipal())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ControllerResponse.ok(response, "Apoderados del estudiante"));
    }

    @PatchMapping("/{id}/apoderados/{apoderadoId}/quitar")
    @PreAuthorize("hasAuthority('MATRICULA_EDITAR')")
    @Operation(summary = "Quitar apoderado de un estudiante")
    public ResponseEntity<ControllerResponse> quitarApoderado(@PathVariable Long id, @PathVariable Long apoderadoId) {
        estudianteApoderadoService.quitarApoderado(id, apoderadoId);
        return ResponseEntity.ok(ControllerResponse.ok(null, "Apoderado removido exitosamente"));
    }
}