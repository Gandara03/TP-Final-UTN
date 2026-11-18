package ar.edu.utn.diplomatura.customerservice.repositorios;

import ar.edu.utn.diplomatura.customerservice.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso a la tabla usuarios (los que se loguean).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
