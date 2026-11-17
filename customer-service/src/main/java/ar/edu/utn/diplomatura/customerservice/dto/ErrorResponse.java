package ar.edu.utn.diplomatura.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Respuesta de error")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(

        @Schema(description = "Codigo HTTP", example = "400")
        int status,

        @Schema(description = "Descripcion del error", example = "Datos invalidos")
        String error,

        @Schema(description = "Errores de validacion por campo (solo en 400 por datos invalidos)",
                example = "{\"cuit\": \"El CUIT/CUIL debe tener 11 digitos, sin guiones\"}")
        Map<String, String> campos,

        @Schema(description = "Fecha y hora del error", example = "2026-11-16T10:15:30")
        String timestamp
) {

    public static ErrorResponse de(int status, String error, Map<String, String> campos) {
        return new ErrorResponse(status, error, campos, LocalDateTime.now().toString());
    }
}
