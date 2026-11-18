package ar.edu.utn.diplomatura.customerservice.integracion;

import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;

import java.util.List;

/**
 * Lo que customer-service necesita de product-service.
 */
public interface ProductoGateway {

    List<ProductoResponse> productosDelCliente(Long clienteId);
}
