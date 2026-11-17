package ar.edu.utn.diplomatura.customerservice.integracion;

import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.customerservice.excepciones.ServicioNoDisponibleException;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Tests del gateway: devuelve lo que trae Feign y traduce sus errores a 503.
 */
@ExtendWith(MockitoExtension.class)
class ProductoGatewayFeignTest {

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private ProductoGatewayFeign productoGateway;

    @Test
    @DisplayName("Devuelve los productos que responde product-service")
    void devuelveLosProductos() {
        List<ProductoResponse> productos = List.of();
        when(productoClient.productosDelCliente(1L)).thenReturn(productos);

        assertSame(productos, productoGateway.productosDelCliente(1L));
    }

    @Test
    @DisplayName("Si Feign falla (servicio caido) lanza ServicioNoDisponibleException")
    void feignFallaDevuelve503() {
        when(productoClient.productosDelCliente(1L)).thenThrow(FeignException.class);

        assertThrows(ServicioNoDisponibleException.class, () -> productoGateway.productosDelCliente(1L));
    }
}
