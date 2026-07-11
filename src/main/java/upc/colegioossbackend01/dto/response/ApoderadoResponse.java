package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upc.colegioossbackend01.enums.TipoDocumento;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApoderadoResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
}