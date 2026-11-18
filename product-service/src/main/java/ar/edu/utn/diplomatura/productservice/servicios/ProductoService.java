package ar.edu.utn.diplomatura.productservice.servicios;

import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;

import java.util.List;

/**
 * Operaciones de productos.
 */
public interface ProductoService {

    List<ProductoResponse> listarTodos();

    List<ProductoResponse> listarPorCliente(Long clienteId);

    ProductoResponse buscarPorId(Long id);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}
