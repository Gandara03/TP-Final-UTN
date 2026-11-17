package ar.edu.utn.diplomatura.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Cliente junto con sus productos")
public record ClienteConProductosResponse(

        @Schema(description = "Datos del cliente")
        ClienteResponse cliente,

        @Schema(description = "Productos activos del cliente (puede estar vacia)")
        List<ProductoResponse> productos
) {
}
