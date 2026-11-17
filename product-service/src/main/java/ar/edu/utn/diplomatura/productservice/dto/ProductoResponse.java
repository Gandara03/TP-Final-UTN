package ar.edu.utn.diplomatura.productservice.dto;

import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Producto devuelto por la API (tambien lo consume customer-service)")
public record ProductoResponse(

        @Schema(description = "Id generado por la base", example = "2")
        Long id,

        @Schema(description = "Id del cliente titular", example = "1")
        Long clienteId,

        @Schema(description = "Tipo de producto", example = "TARJETA")
        TipoProducto tipo,

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
