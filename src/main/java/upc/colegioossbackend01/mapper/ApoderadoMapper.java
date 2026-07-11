package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.ApoderadoResponse;
import upc.colegioossbackend01.entity.Apoderado;

@Component
public class ApoderadoMapper {

    public ApoderadoResponse toResponse(Apoderado apoderado) {
        return ApoderadoResponse.builder()
                .id(apoderado.getId())
                .tipoDocumento(apoderado.getTipoDocumento())
                .numeroDocumento(apoderado.getNumeroDocumento())
                .nombres(apoderado.getNombres())
                .apellidos(apoderado.getApellidos())
                .telefono(apoderado.getTelefono())
                .email(apoderado.getEmail())
                .direccion(apoderado.getDireccion())
                .activo(apoderado.isActivo())
                .build();
    }
}