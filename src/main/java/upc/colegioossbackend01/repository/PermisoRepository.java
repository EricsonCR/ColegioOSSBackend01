package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.colegioossbackend01.entity.Permiso;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Permiso> findByActivoTrue();
}