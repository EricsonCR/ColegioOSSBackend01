package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.AulaCurso;

import java.util.List;

public interface AulaCursoRepository extends JpaRepository<AulaCurso, Long> {

    boolean existsByAulaIdAndCursoId(Long aulaId, Long cursoId);

    List<AulaCurso> findByActivoTrue();

    List<AulaCurso> findByAulaIdAndActivoTrue(Long aulaId);
}