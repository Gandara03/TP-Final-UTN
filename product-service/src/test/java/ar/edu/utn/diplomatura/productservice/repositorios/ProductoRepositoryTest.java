package ar.edu.utn.diplomatura.productservice.repositorios;

import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests del repositorio contra una base H2 en memoria (@DataJpaTest).
 */
@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void cargarDatos() {
        Producto cerrado = new Producto(1L, TipoProducto.CUENTA, "0001-0099", "Cuenta cerrada", BigDecimal.ZERO);
        cerrado.setActivo(false);
        productoRepository.saveAll(List.of(
                new Producto(1L, TipoProducto.CUENTA, "0001-0001", "Caja de ahorro", new BigDecimal("1000")),
                new Producto(1L, TipoProducto.TARJETA, "4517-0001", "Visa Gold", new BigDecimal("800000")),
                new Producto(2L, TipoProducto.PRESTAMO, "PR-0001", "Prestamo personal", new BigDecimal("3000000")),
                cerrado
        ));
    }

    @Test
    @DisplayName("findByClienteIdAndActivoTrue: trae solo los activos del cliente")
    void productosActivosDeUnCliente() {
        List<Producto> productos = productoRepository.findByClienteIdAndActivoTrue(1L);

        assertEquals(2, productos.size());
        assertTrue(productos.stream().allMatch(p -> p.getClienteId().equals(1L) && p.isActivo()));
    }

    @Test
    @DisplayName("findByActivoTrue: no incluye los dados de baja")
    void soloActivos() {
        assertEquals(3, productoRepository.findByActivoTrue().size());
    }

    @Test
    @DisplayName("existsByNumero: detecta numeros usados")
    void existePorNumero() {
        assertTrue(productoRepository.existsByNumero("4517-0001"));
        assertFalse(productoRepository.existsByNumero("9999-9999"));
    }
}
