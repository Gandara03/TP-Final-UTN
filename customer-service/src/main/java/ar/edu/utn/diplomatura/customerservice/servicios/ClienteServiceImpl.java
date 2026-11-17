package ar.edu.utn.diplomatura.customerservice.servicios;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteConProductosResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.customerservice.entidades.Cliente;
import ar.edu.utn.diplomatura.customerservice.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.diplomatura.customerservice.excepciones.RecursoDuplicadoException;
import ar.edu.utn.diplomatura.customerservice.integracion.ProductoGateway;
import ar.edu.utn.diplomatura.customerservice.mappers.ClienteMapper;
import ar.edu.utn.diplomatura.customerservice.repositorios.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio de clientes. Recibe y devuelve DTOs; la conversion la hace el mapper
 * y los productos se piden a product-service a traves de ProductoGateway.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ProductoGateway productoGateway;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper,
                              ProductoGateway productoGateway) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.productoGateway = productoGateway;
    }

    @Override
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::aResponse)
                .toList();
    }

    /** orElseThrow: si no existe lanza ClienteNoEncontradoException (404). */
    @Override
    public ClienteResponse buscarPorId(Long id) {
        return clienteMapper.aResponse(obtener(id));
    }

    @Override
    public ClienteResponse buscarPorCuit(String cuit) {
        return clienteRepository.findByCuit(cuit)
                .map(clienteMapper::aResponse)
                .orElseThrow(() -> new ClienteNoEncontradoException(cuit));
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        validarCuitLibre(request.cuit());
        validarEmailLibre(clienteMapper.normalizarEmail(request.email()));
        Cliente cliente = clienteMapper.aEntidad(request);
        return clienteMapper.aResponse(guardar(cliente));
    }

    /** PUT: reemplaza los datos. Si cambia el CUIT o el email, los nuevos no pueden estar usados. */
    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtener(id);
        if (!cliente.getCuit().equals(request.cuit())) {
            validarCuitLibre(request.cuit());
        }
        String email = clienteMapper.normalizarEmail(request.email());
        if (!cliente.getEmail().equals(email)) {
            validarEmailLibre(email);
        }
        clienteMapper.actualizarEntidad(cliente, request);
        return clienteMapper.aResponse(guardar(cliente));
    }

    /** Primero valida que el cliente exista y despues le pide sus productos a product-service. */
    @Override
    public ClienteConProductosResponse buscarConProductos(Long id) {
        Cliente cliente = obtener(id);
        List<ProductoResponse> productos = productoGateway.productosDelCliente(id);
        return new ClienteConProductosResponse(clienteMapper.aResponse(cliente), productos);
    }

    private Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    private void validarCuitLibre(String cuit) {
        if (clienteRepository.existsByCuit(cuit)) {
            throw new RecursoDuplicadoException("Ya existe un cliente con CUIT/CUIL " + cuit);
        }
    }

    private void validarEmailLibre(String email) {
        if (clienteRepository.existsByEmail(email)) {
            throw new RecursoDuplicadoException("Ya existe un cliente con email " + email);
        }
    }

    // try/catch: si dos altas llegan a la vez con el mismo CUIT o email, la base rechaza la segunda -> 409
    private Cliente guardar(Cliente cliente) {
        try {
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new RecursoDuplicadoException(
                    "Ya existe un cliente con CUIT/CUIL " + cliente.getCuit() + " o email " + cliente.getEmail(), e);
        }
    }
}
