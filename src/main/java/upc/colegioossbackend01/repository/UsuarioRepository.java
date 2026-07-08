package upc.colegioossbackend01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import upc.colegioossbackend01.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol r LEFT JOIN FETCH r.permisos WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRolYPermisos(@Param("username") String username);
}