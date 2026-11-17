package ar.edu.utn.diplomatura.customerservice.config;

import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import ar.edu.utn.diplomatura.customerservice.entidades.Rol;
import ar.edu.utn.diplomatura.customerservice.entidades.Usuario;
import ar.edu.utn.diplomatura.customerservice.repositorios.ClienteRepository;
import ar.edu.utn.diplomatura.customerservice.repositorios.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga usuarios y clientes de ejemplo al arrancar, solo si las tablas estan vacias.
 * Los clientes 1, 2 y 3 son los que tienen productos en product-service.
 */
@Component
public class CargaDatosIniciales implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaDatosIniciales(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // usuarios de prueba para el login (las passwords se guardan hasheadas)
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario("admin", passwordEncoder.encode("admin123"), Rol.ADMIN),
                    new Usuario("usuario", passwordEncoder.encode("usuario123"), Rol.USUARIO)
            ));
        }

        if (clienteRepository.count() == 0) {
            clienteRepository.saveAll(List.of(
                    new Cliente("Alexis", "Gandara", "20450703398", "alexis20143@gmail.com", new BigDecimal("152300.50")),
                    new Cliente("Lionel", "Messi", "20289876546", "lionel.messi@gmail.com", new BigDecimal("1250000")),
                    new Cliente("Enzo", "Fernandez", "20354112230", "enzo.fernandez@gmail.com", BigDecimal.ZERO)
            ));
        }

        System.out.println("=== Datos iniciales: " + usuarioRepository.count() + " usuarios, "
                + clienteRepository.count() + " clientes ===");
    }
}
