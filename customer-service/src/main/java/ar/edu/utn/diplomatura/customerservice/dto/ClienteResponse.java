package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Cliente devuelto por la API")
public record ClienteResponse(

        @Schema(description = "Id generado por la base", example = "1")
        Long id,

        @Schema(description = "Nombre", example = "Alexis")
        String nombre,

        @Schema(description = "Apellido", example = "Gandara")
        String apellido,

        @Schema(description = "CUIT/CUIL", example = "20450703398")
        String cuit,

        @Schema(description = "Email", example = "alexis20143@gmail.com")
        String email,

        @Schema(description = "Saldo actual", example = "152300.50")
        BigDecimal saldo
) {
}
