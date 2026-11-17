package ar.edu.utn.diplomatura.customerservice.repositorios;

import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso a la tabla clientes.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCuit(String cuit);

    boolean existsByCuit(String cuit);

    boolean existsByEmail(String email);
}
