package ar.edu.utn.diplomatura.productservice.controladores;

import ar.edu.utn.diplomatura.productservice.dto.ErrorResponse;
import ar.edu.utn.diplomatura.productservice.dto.ProductoRequest;
import ar.edu.utn.diplomatura.productservice.dto.ProductoResponse;
import ar.edu.utn.diplomatura.productservice.servicios.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * endpoints de Producto
 */
@Tag(name = "Productos")
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar productos", description = "Devuelve los productos activos.")
    @ApiResponse(responseCode = "200", description = "Lista de productos (puede estar vacia)")
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listarTodos();
    }

    /** Este es el endpoint que consume customer-service con Feign. */
    @Operation(summary = "Productos de un cliente", description = "Devuelve los productos activos del cliente indicado.")
    @ApiResponse(responseCode = "200", description = "Lista de productos del cliente (puede estar vacia)")
    @GetMapping("/cliente/{clienteId}")
    public List<ProductoResponse> listarPorCliente(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Long clienteId) {
        return productoService.listarPorCliente(clienteId);
    }

    @Operation(summary = "Buscar producto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe o fue dado de baja",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ProductoResponse buscarPorId(@Parameter(description = "Id del producto", example = "1") @PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @Operation(summary = "Crear producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Falta algun dato o el tipo no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese numero",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @Operation(summary = "Actualizar producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe o fue dado de baja",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El numero nuevo ya esta usado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@Parameter(description = "Id del producto", example = "3") @PathVariable Long id,
                                       @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @Operation(summary = "Dar de baja un producto", description = "Baja logica: el producto queda en la base como inactivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto dado de baja"),
            @ApiResponse(responseCode = "404", description = "No existe o ya fue dado de baja",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "Id del producto", example = "6") @PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
