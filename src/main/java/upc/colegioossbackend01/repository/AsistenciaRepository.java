package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Asistencia;

import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByClaseIdAndMatriculaIdAndActivoTrue(Long claseId, Long matriculaId);

    List<Asistencia> findByClaseIdAndActivoTrue(Long claseId);

    List<Asistencia> findByMatriculaIdAndActivoTrue(Long matriculaId);
}