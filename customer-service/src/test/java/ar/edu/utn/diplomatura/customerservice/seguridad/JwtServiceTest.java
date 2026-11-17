package ar.edu.utn.diplomatura.customerservice.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests de JwtService: generar y validar tokens sin levantar Spring.
 */
class JwtServiceTest {

    private static final String SECRETO = "clave-solo-para-tests-que-tiene-mas-de-32-bytes-123456";

    private final JwtService jwtService = new JwtService(SECRETO, 3600000);

    @Test
    @DisplayName("El token generado trae el usuario y el rol")
    void tokenConUsuarioYRol() {
        String token = jwtService.generarToken("admin", "ADMIN");

        Claims claims = jwtService.validarToken(token);

        assertEquals("admin", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
    }

    @Test
    @DisplayName("Un token firmado con otra clave es rechazado")
    void tokenConOtraClave() {
        JwtService otro = new JwtService("otra-clave-distinta-que-tambien-supera-los-32-bytes", 3600000);
        String token = otro.generarToken("admin", "ADMIN");

        assertThrows(JwtException.class, () -> jwtService.validarToken(token));
    }

    @Test
    @DisplayName("Un token vencido es rechazado")
    void tokenVencido() {
        JwtService vencido = new JwtService(SECRETO, -1000);
        String token = vencido.generarToken("usuario", "USUARIO");

        assertThrows(JwtException.class, () -> jwtService.validarToken(token));
    }
}
