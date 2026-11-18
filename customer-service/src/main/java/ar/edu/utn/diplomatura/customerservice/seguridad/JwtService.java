package ar.edu.utn.diplomatura.customerservice.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Crea y valida los JWT (firma HS256).
 * El secreto llega desde el Config Server, que lo toma de la variable de entorno JWT_SECRET.
 * Tiene que tener al menos 32 caracteres.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMillis;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiration-millis}") long expiracionMillis) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMillis = expiracionMillis;
    }

    /** Arma el token con el usuario (sub), su rol, la fecha de emision y la de vencimiento. */
    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMillis))
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    /** Si la firma no coincide o el token vencio, lanza JwtException. */
    public Claims validarToken(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMillis / 1000;
    }
}
