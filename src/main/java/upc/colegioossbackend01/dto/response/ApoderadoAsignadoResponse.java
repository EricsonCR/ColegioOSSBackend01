package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApoderadoAsignadoResponse {

    private ApoderadoResponse apoderado;
    private String parentesco;
    private boolean esPrincipal;
}