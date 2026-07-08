package upc.colegioossbackend01.dto.request;

import jakarta.validation.constraints.NotEmpty;
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
public class AsignarPermisosRequest {

    @NotEmpty(message = "Debe indicar al menos un permiso")
    private List<Long> permisoIds;
}