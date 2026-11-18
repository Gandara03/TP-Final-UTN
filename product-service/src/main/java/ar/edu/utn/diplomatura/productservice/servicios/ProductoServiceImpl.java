package ar.edu.utn.diplomatura.productservice.servicios;

import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.productservice.entidades.Producto;
import ar.edu.utn.diplomatura.productservice.excepciones.ProductoNoEncontradoException;
import ar.edu.utn.diplomatura.productservice.excepciones.RecursoDuplicadoException;
import ar.edu.utn.diplomatura.productservice.mappers.ProductoMapper;
import ar.edu.utn.diplomatura.productservice.repositorios.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio de productos. Recibe y devuelve DTOs; la conversion la hace el mapper.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    /** Solo los activos: los dados de baja logica no se listan. */
    @Override
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findByActivoTrue().stream()
                .map(productoMapper::aResponse)
                .toList();
    }

    /** Productos activos de un cliente. Si no tiene ninguno devuelve una lista vacia. */
    @Override
    public List<ProductoResponse> listarPorCliente(Long clienteId) {
        return productoRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(productoMapper::aResponse)
                .toList();
    }

    @Override
    public ProductoResponse buscarPorId(Long id) {
        return productoMapper.aResponse(obtenerActivo(id));
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        validarNumeroLibre(request.numero());
        Producto producto = productoMapper.aEntidad(request);
        return productoMapper.aResponse(guardar(producto));
    }

    /** PUT: reemplaza los datos. Si cambia el numero, el nuevo no puede estar usado. */
    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerActivo(id);
        if (!producto.getNumero().equals(request.numero())) {
            validarNumeroLibre(request.numero());
        }
        productoMapper.actualizarEntidad(producto, request);
        return productoMapper.aResponse(guardar(producto));
    }

    /** DELETE: baja logica, el producto queda en la base como inactivo. */
    @Override
    public void eliminar(Long id) {
        Producto producto = obtenerActivo(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    /** Si no existe o fue dado de baja -> 404. */
    private Producto obtenerActivo(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    private void validarNumeroLibre(String numero) {
        if (productoRepository.existsByNumero(numero)) {
            throw new RecursoDuplicadoException("Ya existe un producto con numero " + numero);
        }
    }

    // try/catch: si dos pedidos llegan con el mismo numero, la base rechaza el segundo -> 409
    private Producto guardar(Producto producto) {
        try {
            return productoRepository.save(producto);
        } catch (DataIntegrityViolationException e) {
            throw new RecursoDuplicadoException("Ya existe un producto con numero " + producto.getNumero(), e);
        }
    }
}
