package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Datos para registrar o actualizar un cliente")
public record ClienteRequest(

        @Schema(description = "Nombre", example = "Sofia", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(description = "Apellido", example = "Martinez", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @Schema(description = "CUIT/CUIL sin guiones (11 digitos), unico", example = "27356789011",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El CUIT/CUIL es obligatorio")
        @Pattern(regexp = "\\d{11}", message = "El CUIT/CUIL debe tener 11 digitos, sin guiones")
        String cuit,

        @Schema(description = "Email, unico", example = "sofia.martinez@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String email,

        @Schema(description = "Saldo del cliente", example = "85000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El saldo es obligatorio")
        @PositiveOrZero(message = "El saldo no puede ser negativo")
        BigDecimal saldo
) {
}
