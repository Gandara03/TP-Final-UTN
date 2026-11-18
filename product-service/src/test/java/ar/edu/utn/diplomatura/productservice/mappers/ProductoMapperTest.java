package ar.edu.utn.diplomatura.productservice.mappers;

import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import ar.edu.utn.diplomatura.productservice.entidades.TipoProducto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests del mapper: que copie bien los campos de DTO a entidad y de entidad a DTO.
 */
class ProductoMapperTest {

    private final ProductoMapper mapper = new ProductoMapper();

    @Test
    @DisplayName("De request a entidad y de entidad a response sin perder datos")
    void idaYVuelta() {
        ProductoRequest request = new ProductoRequest(2L, TipoProducto.PRESTAMO, "PR-0009",
                "Prestamo personal", new BigDecimal("1500000"));

        Producto entidad = mapper.aEntidad(request);
        entidad.setId(7L);
        ProductoResponse response = mapper.aResponse(entidad);

        assertTrue(entidad.isActivo());
        assertEquals(LocalDate.now(), entidad.getFechaAlta());
        assertEquals(7L, response.id());
        assertEquals(2L, response.clienteId());
        assertEquals(TipoProducto.PRESTAMO, response.tipo());
        assertEquals("PR-0009", response.numero());
        assertEquals("Prestamo personal", response.descripcion());
    }
}
