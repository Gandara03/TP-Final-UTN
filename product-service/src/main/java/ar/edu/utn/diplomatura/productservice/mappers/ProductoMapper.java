package ar.edu.utn.diplomatura.productservice.mappers;

import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import org.springframework.stereotype.Component;

/**
 * Pasa de DTO a entidad y de entidad a DTO. Asi la entidad nunca sale ni entra por la API.
 */
@Component
public class ProductoMapper {

    public Producto aEntidad(ProductoRequest request) {
        return new Producto(request.clienteId(), request.tipo(), request.numero(),
                request.descripcion(), request.monto());
    }

    /** Usado en el PUT: copia los datos del request sobre un producto existente. */
    public void actualizarEntidad(Producto producto, ProductoRequest request) {
        producto.setClienteId(request.clienteId());
        producto.setTipo(request.tipo());
        producto.setNumero(request.numero());
        producto.setDescripcion(request.descripcion());
        producto.setMonto(request.monto());
    }

    public ProductoResponse aResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getClienteId(),
                producto.getTipo(),
                producto.getNumero(),
                producto.getDescripcion(),
                producto.getMonto(),
                producto.getFechaAlta()
        );
    }
}
