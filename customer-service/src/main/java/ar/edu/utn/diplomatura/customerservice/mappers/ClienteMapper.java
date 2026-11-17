package ar.edu.utn.diplomatura.customerservice.mappers;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;
import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import org.springframework.stereotype.Component;

/**
 * Pasa de DTO a entidad y de entidad a DTO. Asi la entidad nunca sale ni entra por la API.
 */
@Component
public class ClienteMapper {

    public Cliente aEntidad(ClienteRequest request) {
        return new Cliente(request.nombre(), request.apellido(), request.cuit(),
                normalizarEmail(request.email()), request.saldo());
    }

    /** Usado en el PUT: copia los datos del request sobre un cliente existente. */
    public void actualizarEntidad(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setCuit(request.cuit());
        cliente.setEmail(normalizarEmail(request.email()));
        cliente.setSaldo(request.saldo());
    }

    public ClienteResponse aResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getCuit(),
                cliente.getEmail(),
                cliente.getSaldo()
        );
    }

    /** El email se guarda en minuscula para que "Juan@Gmail.com" y "juan@gmail.com" sean el mismo. */
    public String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
