package upc.colegioossbackend01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Verificación de estado del servicio")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Confirma que el servicio está corriendo")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Colegio OSS Backend funcionando");
    }
}