package ar.edu.utn.diplomatura.customerservice.excepciones;

/**
 * Excepcion custom: el cliente no existe -> 404.
 */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long id) {
        super("No existe un cliente con id " + id);
    }

    public ClienteNoEncontradoException(String cuit) {
        super("No existe un cliente con CUIT/CUIL " + cuit);
    }
}
