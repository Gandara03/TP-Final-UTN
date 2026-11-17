package ar.edu.utn.diplomatura.customerservice.excepciones;

import ar.edu.utn.diplomatura.customerservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Atrapa las excepciones de todos los controladores y devuelve un ErrorResponse
 * con el codigo HTTP que corresponde, en vez de un error 500.
 */
@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {

    /** 404: cliente inexistente. */
    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(ClienteNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /** 409: CUIT/CUIL o email repetido. */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> duplicado(RecursoDuplicadoException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    /** 503: product-service no esta disponible. */
    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> servicioNoDisponible(ServicioNoDisponibleException ex) {
        return respuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), null);
    }

    /** 401: usuario o password incorrectos en el login. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credencialesInvalidas(AuthenticationException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Usuario o password incorrectos", null);
    }

    /** 400: fallo alguna validacion del DTO (@NotNull, @NotBlank, ...) marcada con @Valid. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Datos invalidos", campos);
    }

    /** 400: JSON mal formado (por ejemplo un saldo con letras). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es valido", null);
    }

    /** 400: un id que no es numero, por ejemplo /clientes/abc. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "El parametro '" + ex.getName() + "' no es valido", null);
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje, Map<String, String> campos) {
        return ResponseEntity.status(status).body(ErrorResponse.de(status.value(), mensaje, campos));
    }
}
