package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Apoderado;

import java.util.List;
import java.util.Optional;

public interface ApoderadoRepository extends JpaRepository<Apoderado, Long> {

    Optional<Apoderado> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    List<Apoderado> findByActivoTrue();
}