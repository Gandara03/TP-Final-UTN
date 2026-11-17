package ar.edu.utn.diplomatura.customerservice.servicios;

import ar.edu.utn.diplomatura.customerservice.dto.LoginRequest;
import ar.edu.utn.diplomatura.customerservice.dto.LoginResponse;
import ar.edu.utn.diplomatura.customerservice.seguridad.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Valida usuario y password con Spring Security y, si son correctos, genera el JWT.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Si las credenciales no coinciden, authenticate tira BadCredentialsException (401). */
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String rol = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        String token = jwtService.generarToken(auth.getName(), rol);
        return new LoginResponse(token, "Bearer", jwtService.getExpiracionSegundos(), rol);
    }
}
