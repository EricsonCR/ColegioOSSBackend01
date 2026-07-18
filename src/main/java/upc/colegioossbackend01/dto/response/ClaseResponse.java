package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaseResponse {

    private Long id;
    private Long aulaCursoId;
    private String cursoNombre;
    private LocalDate fecha;
    private String tema;
    private String observacion;
    private boolean activo;
}