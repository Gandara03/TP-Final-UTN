package ar.edu.utn.diplomatura.customerservice.servicios;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteConProductosResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import ar.edu.utn.diplomatura.customerservice.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.diplomatura.customerservice.excepciones.RecursoDuplicadoException;
import ar.edu.utn.diplomatura.customerservice.excepciones.ServicioNoDisponibleException;
import ar.edu.utn.diplomatura.customerservice.integracion.ProductoGateway;
import ar.edu.utn.diplomatura.customerservice.mappers.ClienteMapper;
import ar.edu.utn.diplomatura.customerservice.repositorios.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests de ClienteServiceImpl con el repositorio y product-service mockeados (el mapper es el real).
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoGateway productoGateway;

    @Spy
    private ClienteMapper clienteMapper = new ClienteMapper();

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    @DisplayName("Crear: guarda el cliente con el email en minuscula")
    void crearCliente() {
        when(clienteRepository.existsByCuit("27356789011")).thenReturn(false);
        when(clienteRepository.existsByEmail("sofia.martinez@gmail.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(4L);
            return c;
        });

        ClienteResponse creado = clienteService.crear(request("27356789011", "Sofia.Martinez@Gmail.com"));

        assertEquals(4L, creado.id());
        assertEquals("sofia.martinez@gmail.com", creado.email());
    }

    @Test
    @DisplayName("Crear: con un CUIT/CUIL repetido lanza RecursoDuplicadoException y no guarda")
    void crearConCuitRepetido() {
        when(clienteRepository.existsByCuit("20450703398")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class,
                () -> clienteService.crear(request("20450703398", "otro.mail@gmail.com")));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear: con un email repetido lanza RecursoDuplicadoException")
    void crearConEmailRepetido() {
        when(clienteRepository.existsByCuit("27356789011")).thenReturn(false);
        when(clienteRepository.existsByEmail("alexis20143@gmail.com")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class,
                () -> clienteService.crear(request("27356789011", "alexis20143@gmail.com")));
    }

    // Caso de carrera: las validaciones pasaron pero la base rechaza por la restriccion unique.
    @Test
    @DisplayName("Crear: si la base rechaza el dato unico tambien devuelve 409")
    void crearConViolacionDeIntegridad() {
        when(clienteRepository.save(any(Cliente.class))).thenThrow(new DataIntegrityViolationException("unique"));

        assertThrows(RecursoDuplicadoException.class,
                () -> clienteService.crear(request("27356789011", "sofia.martinez@gmail.com")));
    }

    @Test
    @DisplayName("Buscar por id: si no existe lanza ClienteNoEncontradoException")
    void buscarClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () -> clienteService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Buscar por CUIT/CUIL: devuelve el cliente si existe")
    void buscarPorCuit() {
        when(clienteRepository.findByCuit("20450703398")).thenReturn(Optional.of(alexis()));

        assertEquals("Alexis", clienteService.buscarPorCuit("20450703398").nombre());
    }

    @Test
    @DisplayName("Actualizar: cambia el saldo sin validar de nuevo el mismo CUIT/CUIL")
    void actualizarCliente() {
        Cliente existente = alexis();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(existente)).thenReturn(existente);

        ClienteRequest cambios = new ClienteRequest("Alexis", "Gandara", "20450703398",
                "alexis20143@gmail.com", new BigDecimal("99000"));
        ClienteResponse actualizado = clienteService.actualizar(1L, cambios);

        assertEquals(0, new BigDecimal("99000").compareTo(actualizado.saldo()));
        verify(clienteRepository, never()).existsByCuit(any());
    }

    @Test
    @DisplayName("Cliente con productos: junta los datos del cliente con los de product-service")
    void buscarConProductos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(alexis()));
        when(productoGateway.productosDelCliente(1L)).thenReturn(List.of(
                new ProductoResponse(2L, "TARJETA", "4517-0001", "Visa Gold", new BigDecimal("800000"), LocalDate.now())));

        ClienteConProductosResponse resultado = clienteService.buscarConProductos(1L);

        assertEquals(1L, resultado.cliente().id());
        assertEquals(1, resultado.productos().size());
        assertEquals("TARJETA", resultado.productos().get(0).tipo());
    }

    @Test
    @DisplayName("Cliente con productos: si product-service no responde se propaga el 503")
    void buscarConProductosServicioCaido() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(alexis()));
        when(productoGateway.productosDelCliente(1L))
                .thenThrow(new ServicioNoDisponibleException("product-service no esta disponible", null));

        assertThrows(ServicioNoDisponibleException.class, () -> clienteService.buscarConProductos(1L));
    }

    @Test
    @DisplayName("Cliente con productos: si el cliente no existe ni siquiera llama a product-service")
    void buscarConProductosClienteInexistente() {
        when(clienteRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () -> clienteService.buscarConProductos(9L));
        verify(productoGateway, never()).productosDelCliente(anyLong());
    }

    private ClienteRequest request(String cuit, String email) {
        return new ClienteRequest("Sofia", "Martinez", cuit, email, new BigDecimal("85000"));
    }

    private Cliente alexis() {
        Cliente c = new Cliente("Alexis", "Gandara", "20450703398", "alexis20143@gmail.com", new BigDecimal("152300.50"));
        c.setId(1L);
        return c;
    }
}
