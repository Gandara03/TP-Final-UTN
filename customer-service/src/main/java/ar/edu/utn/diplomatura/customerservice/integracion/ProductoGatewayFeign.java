package ar.edu.utn.diplomatura.customerservice.integracion;

import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.customerservice.excepciones.ServicioNoDisponibleException;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementacion con Feign. Traduce los errores de Feign a una excepcion propia (503).
 */
@Component
public class ProductoGatewayFeign implements ProductoGateway {

    private final ProductoClient productoClient;

    public ProductoGatewayFeign(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    @Override
    public List<ProductoResponse> productosDelCliente(Long clienteId) {
        try {
            return productoClient.productosDelCliente(clienteId);
        } catch (FeignException e) {
            // servicio caido, sin instancias en Eureka o error del otro lado
            throw new ServicioNoDisponibleException(
                    "No se pudieron obtener los productos: product-service no esta disponible", e);
        }
    }
}
