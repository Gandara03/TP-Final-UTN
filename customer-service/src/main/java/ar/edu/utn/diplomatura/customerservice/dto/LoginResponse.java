package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token de acceso. Se envia en el header Authorization: Bearer <token>")
public record LoginResponse(

        @Schema(description = "JWT firmado", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        @Schema(description = "Segundos hasta que vence", example = "3600")
        long expiraEnSegundos,

        @Schema(description = "Rol del usuario", example = "ADMIN")
        String rol
) {
}
