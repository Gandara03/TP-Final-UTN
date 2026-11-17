package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesion")
public record LoginRequest(

        @Schema(description = "Usuario", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @Schema(description = "Password", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La password es obligatoria")
        String password
) {
}
