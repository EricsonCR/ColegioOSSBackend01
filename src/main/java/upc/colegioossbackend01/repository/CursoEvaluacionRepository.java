package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.CursoEvaluacion;

import java.util.List;

public interface CursoEvaluacionRepository extends JpaRepository<CursoEvaluacion, Long> {

    List<CursoEvaluacion> findByAulaCursoIdAndBimestreAndActivoTrue(Long aulaCursoId, Integer bimestre);

    List<CursoEvaluacion> findByAulaCursoIdAndActivoTrue(Long aulaCursoId);

    List<CursoEvaluacion> findByAulaCursoIdAndBimestreAndActivoTrueAndIdNot(Long aulaCursoId, Integer bimestre, Long id);
}