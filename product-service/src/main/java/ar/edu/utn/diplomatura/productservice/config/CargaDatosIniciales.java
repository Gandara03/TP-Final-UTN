package ar.edu.utn.diplomatura.productservice.config;

import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import ar.edu.utn.diplomatura.productservice.repositorios.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga productos de ejemplo al arrancar, solo si la tabla esta vacia.
 * Los clienteId 1, 2 y 3 son los clientes que carga customer-service y se relacionan con estos
 */
@Component
public class CargaDatosIniciales implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public CargaDatosIniciales(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }

        productoRepository.saveAll(List.of(
                // Alexis (cliente 1): caja de ahorro, tarjeta y plazo fijo
                new Producto(1L, TipoProducto.CUENTA, "0001-0001", "Caja de ahorro en pesos", new BigDecimal("152300.50")),
                new Producto(1L, TipoProducto.TARJETA, "4517-0001", "Visa Gold", new BigDecimal("800000")),
                new Producto(1L, TipoProducto.INVERSION, "PF-0001", "Plazo fijo 30 dias", new BigDecimal("500000")),
                // Lionel (cliente 2): cuenta corriente y un prestamo
                new Producto(2L, TipoProducto.CUENTA, "0002-0001", "Cuenta corriente", new BigDecimal("1250000")),
                new Producto(2L, TipoProducto.PRESTAMO, "PR-0001", "Prestamo personal 24 cuotas", new BigDecimal("3000000")),
                // Enzo (cliente 3): caja de ahorro sin saldo
                new Producto(3L, TipoProducto.CUENTA, "0003-0001", "Caja de ahorro en pesos", BigDecimal.ZERO)
        ));

        System.out.println("=== Datos iniciales: " + productoRepository.count() + " productos ===");
    }
}
