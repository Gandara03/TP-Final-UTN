package ar.edu.utn.diplomatura.customerservice.servicios;

import ar.edu.utn.diplomatura.customerservice.dto.LoginRequest;
import ar.edu.utn.diplomatura.customerservice.dto.LoginResponse;

/**
 * Login de usuarios.
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);
}
