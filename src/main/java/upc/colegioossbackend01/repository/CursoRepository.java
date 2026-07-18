package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    boolean existsByCodigo(String codigo);

    List<Curso> findByActivoTrue();

    Optional<Curso> findByCodigo(String codigo);
}