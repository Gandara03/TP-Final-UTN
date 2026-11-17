package ar.edu.utn.diplomatura.customerservice.servicios;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteConProductosResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;

import java.util.List;

/**
 * Operaciones de clientes.
 */
public interface ClienteService {

    List<ClienteResponse> listarTodos();

    ClienteResponse buscarPorId(Long id);

    ClienteResponse buscarPorCuit(String cuit);

    ClienteResponse crear(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    ClienteConProductosResponse buscarConProductos(Long id);
}
