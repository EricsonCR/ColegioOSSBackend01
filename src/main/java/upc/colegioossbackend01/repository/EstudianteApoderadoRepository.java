package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.EstudianteApoderado;

import java.util.List;
import java.util.Optional;

public interface EstudianteApoderadoRepository extends JpaRepository<EstudianteApoderado, Long> {

    List<EstudianteApoderado> findByEstudianteIdAndActivoTrue(Long estudianteId);

    Optional<EstudianteApoderado> findByEstudianteIdAndApoderadoIdAndActivoTrue(Long estudianteId, Long apoderadoId);
}