package ar.edu.utn.diplomatura.productservice.excepciones;

/**
 * Excepcion custom: el producto no existe o fue dado de baja -> 404.
 */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("No existe un producto con id " + id);
    }
}
