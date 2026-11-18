package ar.edu.utn.diplomatura.customerservice.excepciones;

/**
 * Excepcion custom: CUIT/CUIL o email repetido -> 409.
 */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public RecursoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
