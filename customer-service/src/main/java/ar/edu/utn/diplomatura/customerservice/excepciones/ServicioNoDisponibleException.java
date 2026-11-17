package ar.edu.utn.diplomatura.customerservice.excepciones;

/**
 * Excepcion custom: product-service no respondio (caido o no registrado en Eureka) -> 503.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
