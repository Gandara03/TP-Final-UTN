package ar.edu.utn.diplomatura.productservice.excepciones;

/**
 * Excepcion custom: numero de producto repetido -> 409.
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public RecursoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
