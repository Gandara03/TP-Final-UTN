package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Producto del cliente, obtenido de product-service")
public record ProductoResponse(

        @Schema(description = "Id del producto", example = "2")
        Long id,

        @Schema(description = "Tipo: CUENTA, TARJETA, PRESTAMO o INVERSION", example = "TARJETA")
        String tipo,

        @Schema(description = "Numero del producto", example = "4517-0001")
        String numero,

        @Schema(description = "Descripcion", example = "Visa Gold")
        String descripcion,

        @Schema(description = "Saldo, limite, capital o monto invertido", example = "800000.00")
        BigDecimal monto,

        @Schema(description = "Fecha de alta", example = "2026-11-16")
        LocalDate fechaAlta
) {
}
