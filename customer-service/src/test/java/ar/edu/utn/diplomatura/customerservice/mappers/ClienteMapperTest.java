package ar.edu.utn.diplomatura.customerservice.mappers;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;
import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests del mapper: que copie bien los campos de DTO a entidad y de entidad a DTO.
 */
class ClienteMapperTest {

    private final ClienteMapper mapper = new ClienteMapper();

    @Test
    @DisplayName("De request a entidad y de entidad a response sin perder datos")
    void idaYVuelta() {
        ClienteRequest request = new ClienteRequest("Lionel", "Messi", "20289876546",
                " Lionel.Messi@Gmail.com ", new BigDecimal("1250000"));

        Cliente entidad = mapper.aEntidad(request);
        entidad.setId(2L);
        ClienteResponse response = mapper.aResponse(entidad);

        assertEquals(2L, response.id());
        assertEquals("Lionel", response.nombre());
        assertEquals("Messi", response.apellido());
        assertEquals("20289876546", response.cuit());
        assertEquals("lionel.messi@gmail.com", response.email());
        assertEquals(0, new BigDecimal("1250000").compareTo(response.saldo()));
    }
}
