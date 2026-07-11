package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.EstudianteResponse;
import upc.colegioossbackend01.entity.Estudiante;

@Component
public class EstudianteMapper {

    public EstudianteResponse toResponse(Estudiante estudiante) {
        return EstudianteResponse.builder()
                .id(estudiante.getId())
                .tipoDocumento(estudiante.getTipoDocumento())
                .numeroDocumento(estudiante.getNumeroDocumento())
                .nombres(estudiante.getNombres())
                .apellidos(estudiante.getApellidos())
                .fechaNacimiento(estudiante.getFechaNacimiento())
                .genero(estudiante.getGenero())
                .direccion(estudiante.getDireccion())
                .activo(estudiante.isActivo())
                .build();
    }
}