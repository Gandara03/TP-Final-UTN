package ar.edu.utn.diplomatura.customerservice.repositorios;

import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests del repositorio contra una base H2 en memoria (@DataJpaTest).
 */
@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void cargarDatos() {
        clienteRepository.save(new Cliente("Alexis", "Gandara", "20450703398", "alexis20143@gmail.com",
                new BigDecimal("152300.50")));
    }

    @Test
    @DisplayName("findByCuit: encuentra al cliente por su CUIT/CUIL")
    void buscarPorCuit() {
        assertEquals("Alexis", clienteRepository.findByCuit("20450703398").orElseThrow().getNombre());
        assertTrue(clienteRepository.findByCuit("20999999999").isEmpty());
    }

    @Test
    @DisplayName("existsByCuit y existsByEmail: detectan datos ya usados")
    void existenCuitYEmail() {
        assertTrue(clienteRepository.existsByCuit("20450703398"));
        assertTrue(clienteRepository.existsByEmail("alexis20143@gmail.com"));
        assertFalse(clienteRepository.existsByEmail("nadie@gmail.com"));
    }

    // La columna cuit es unique: la base no deja guardar dos clientes con el mismo.
    @Test
    @DisplayName("No se pueden guardar dos clientes con el mismo CUIT/CUIL")
    void cuitUnico() {
        Cliente repetido = new Cliente("Otro", "Cliente", "20450703398", "otro@gmail.com", BigDecimal.ZERO);

        assertThrows(DataIntegrityViolationException.class, () -> clienteRepository.saveAndFlush(repetido));
    }
}
