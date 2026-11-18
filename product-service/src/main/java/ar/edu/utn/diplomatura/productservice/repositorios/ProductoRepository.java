package ar.edu.utn.diplomatura.productservice.repositorios;

import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a la tabla productos. Spring arma el SQL a partir del nombre de cada metodo.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    Optional<Producto> findByIdAndActivoTrue(Long id);

    List<Producto> findByClienteIdAndActivoTrue(Long clienteId);

    boolean existsByNumero(String numero);
}
