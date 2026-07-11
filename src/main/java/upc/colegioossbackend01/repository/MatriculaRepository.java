package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByEstudianteIdAndPeriodoAndEstado(Long estudianteId, Integer periodo, EstadoMatricula estado);

    List<Matricula> findByEstudianteId(Long estudianteId);
}