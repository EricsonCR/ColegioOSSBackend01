package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.DocenteCurso;

import java.util.List;

public interface DocenteCursoRepository extends JpaRepository<DocenteCurso, Long> {

    boolean existsByAulaCursoIdAndUsuarioId(Long aulaCursoId, Long usuarioId);

    List<DocenteCurso> findByUsuarioIdAndActivoTrue(Long usuarioId);

    List<DocenteCurso> findByAulaCursoIdAndActivoTrue(Long aulaCursoId);
}