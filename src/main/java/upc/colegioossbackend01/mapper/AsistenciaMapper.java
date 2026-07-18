package upc.colegioossbackend01.mapper;

import org.springframework.stereotype.Component;
import upc.colegioossbackend01.dto.response.AsistenciaResponse;
import upc.colegioossbackend01.entity.Asistencia;

@Component
public class AsistenciaMapper {

    public AsistenciaResponse toResponse(Asistencia asistencia) {
        return AsistenciaResponse.builder()
                .id(asistencia.getId())
                .claseId(asistencia.getClase().getId())
                .matriculaId(asistencia.getMatricula().getId())
                .estudianteNombre(asistencia.getMatricula().getEstudiante().getNombres() + " " + asistencia.getMatricula().getEstudiante().getApellidos())
                .estado(asistencia.getEstado())
                .observacion(asistencia.getObservacion())
                .activo(asistencia.isActivo())
                .build();
    }
}