package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Evaluacion;

import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    boolean existsByCodigo(String codigo);

    List<Evaluacion> findByActivoTrue();
}