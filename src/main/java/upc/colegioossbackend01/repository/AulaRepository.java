package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Aula;
import upc.colegioossbackend01.enums.Nivel;

import java.util.List;
import java.util.Optional;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    boolean existsByPeriodoAndNivelAndGradoAndSeccion(Integer periodo, upc.colegioossbackend01.enums.Nivel nivel, Integer grado, String seccion);

    List<Aula> findByActivoTrue();

    Optional<Aula> findByPeriodoAndNivelAndGradoAndSeccion(Integer periodo, Nivel nivel, Integer grado, String seccion);
}