package ar.edu.utn.diplomatura.customerservice.seguridad;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Reglas de seguridad: login publico, GET para ADMIN y USUARIO, POST y PUT solo ADMIN.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Spring reenvia los errores a /error: si no es publico, un 403 termina como 401
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes/**").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers(HttpMethod.POST, "/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // Sin token (o token invalido) -> 401. Con token pero sin el rol -> 403.
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) ->
                                escribirError(response, HttpStatus.UNAUTHORIZED, "Falta el token o no es valido"))
                        .accessDeniedHandler((request, response, ex) ->
                                escribirError(response, HttpStatus.FORBIDDEN, "Tu rol no tiene permiso para esta operacion")))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Responde con el mismo formato que ManejadorGlobalDeExcepciones. */
    private void escribirError(HttpServletResponse response, HttpStatus status, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status.value()
                + ",\"error\":\"" + mensaje
                + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
