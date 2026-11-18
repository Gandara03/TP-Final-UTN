package ar.edu.utn.diplomatura.productservice.dto;

import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Datos para crear o actualizar un producto")
public record ProductoRequest(

        @Schema(description = "Id del cliente titular (vive en customer-service)", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El id del cliente es obligatorio")
        Long clienteId,

        @Schema(description = "Tipo de producto", example = "TARJETA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El tipo es obligatorio (CUENTA, TARJETA, PRESTAMO o INVERSION)")
        TipoProducto tipo,

        @Schema(description = "Numero del producto, unico", example = "5412-0001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El numero es obligatorio")
        String numero,

        @Schema(description = "Descripcion", example = "Mastercard Black")
        String descripcion,

        @Schema(description = "Saldo, limite, capital o monto invertido segun el tipo", example = "1500000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El monto es obligatorio")
        @PositiveOrZero(message = "El monto no puede ser negativo")
        BigDecimal monto
) {
}
