package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import upc.colegioossbackend01.entity.Matricula;
import upc.colegioossbackend01.enums.EstadoMatricula;
import upc.colegioossbackend01.enums.Nivel;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByEstudianteIdAndPeriodoAndEstado(Long estudianteId, Integer periodo, EstadoMatricula estado);

    List<Matricula> findByEstudianteId(Long estudianteId);

    @Query("SELECT m FROM Matricula m LEFT JOIN FETCH m.estudiante " +
            "WHERE (:periodo IS NULL OR m.periodo = :periodo) " +
            "AND (:nivel IS NULL OR m.nivel = :nivel) " +
            "AND (:grado IS NULL OR m.grado = :grado) " +
            "AND (:estado IS NULL OR m.estado = :estado)")
    List<Matricula> buscarConFiltros(@Param("periodo") Integer periodo,
                                     @Param("nivel") Nivel nivel,
                                     @Param("grado") Integer grado,
                                     @Param("estado") EstadoMatricula estado);
}