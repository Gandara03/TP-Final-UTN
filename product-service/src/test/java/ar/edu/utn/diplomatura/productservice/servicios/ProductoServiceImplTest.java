package ar.edu.utn.diplomatura.productservice.servicios;

import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import ar.edu.utn.diplomatura.productservice.excepciones.ProductoNoEncontradoException;
import ar.edu.utn.diplomatura.productservice.excepciones.RecursoDuplicadoException;
import ar.edu.utn.diplomatura.productservice.mappers.ProductoMapper;
import ar.edu.utn.diplomatura.productservice.repositorios.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests de ProductoServiceImpl con el repositorio mockeado (el mapper es el real).
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Spy
    private ProductoMapper productoMapper = new ProductoMapper();

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    @DisplayName("Listar por cliente: devuelve solo los productos de ese cliente")
    void listarPorCliente() {
        Producto visa = producto(10L, 1L, "4517-0001");
        when(productoRepository.findByClienteIdAndActivoTrue(1L)).thenReturn(List.of(visa));

        List<ProductoResponse> resultado = productoService.listarPorCliente(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).clienteId());
        assertEquals("4517-0001", resultado.get(0).numero());
    }

    @Test
    @DisplayName("Listar por cliente: si no tiene productos devuelve una lista vacia")
    void listarPorClienteSinProductos() {
        when(productoRepository.findByClienteIdAndActivoTrue(99L)).thenReturn(List.of());

        assertTrue(productoService.listarPorCliente(99L).isEmpty());
    }

    @Test
    @DisplayName("Buscar por id: si no existe lanza ProductoNoEncontradoException")
    void buscarProductoInexistente() {
        when(productoRepository.findByIdAndActivoTrue(5L)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class, () -> productoService.buscarPorId(5L));
    }

    @Test
    @DisplayName("Crear: guarda el producto y devuelve el DTO con el id generado")
    void crearProducto() {
        when(productoRepository.existsByNumero("5412-0001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(20L);
            return p;
        });

        ProductoResponse creado = productoService.crear(request("5412-0001"));

        assertEquals(20L, creado.id());
        assertEquals(TipoProducto.TARJETA, creado.tipo());
    }

    @Test
    @DisplayName("Crear: con un numero repetido lanza RecursoDuplicadoException y no guarda")
    void crearConNumeroRepetido() {
        when(productoRepository.existsByNumero("4517-0001")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> productoService.crear(request("4517-0001")));
        verify(productoRepository, never()).save(any());
    }

    // Caso de carrera: el exists dio false pero la base rechaza por la restriccion unique.
    @Test
    @DisplayName("Crear: si la base rechaza el numero unico tambien devuelve 409")
    void crearConViolacionDeIntegridad() {
        when(productoRepository.existsByNumero("5412-0002")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenThrow(new DataIntegrityViolationException("unique"));

        assertThrows(RecursoDuplicadoException.class, () -> productoService.crear(request("5412-0002")));
    }

    @Test
    @DisplayName("Actualizar: cambia los datos del producto")
    void actualizarProducto() {
        Producto existente = producto(3L, 1L, "PF-0001");
        when(productoRepository.findByIdAndActivoTrue(3L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(existente)).thenReturn(existente);

        ProductoRequest cambios = new ProductoRequest(1L, TipoProducto.INVERSION, "PF-0001",
                "Plazo fijo 60 dias", new BigDecimal("650000"));
        ProductoResponse actualizado = productoService.actualizar(3L, cambios);

        assertEquals(TipoProducto.INVERSION, actualizado.tipo());
        assertEquals(0, new BigDecimal("650000").compareTo(actualizado.monto()));
    }

    @Test
    @DisplayName("Eliminar: hace baja logica (activo = false)")
    void eliminarHaceBajaLogica() {
        Producto existente = producto(6L, 3L, "0003-0001");
        when(productoRepository.findByIdAndActivoTrue(6L)).thenReturn(Optional.of(existente));

        productoService.eliminar(6L);

        assertFalse(existente.isActivo());
        verify(productoRepository).save(existente);
    }

    private ProductoRequest request(String numero) {
        return new ProductoRequest(2L, TipoProducto.TARJETA, numero, "Mastercard Black", new BigDecimal("1500000"));
    }

    private Producto producto(Long id, Long clienteId, String numero) {
        Producto p = new Producto(clienteId, TipoProducto.TARJETA, numero, "Visa Gold", new BigDecimal("800000"));
        p.setId(id);
        return p;
    }
}
