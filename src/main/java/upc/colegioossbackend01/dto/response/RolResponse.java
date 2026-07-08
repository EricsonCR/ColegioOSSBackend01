package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<PermisoResponse> permisos;
}