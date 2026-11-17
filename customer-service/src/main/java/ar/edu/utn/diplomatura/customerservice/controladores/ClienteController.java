package ar.edu.utn.diplomatura.customerservice.controladores;

import ar.edu.utn.diplomatura.customerservice.dto.ClienteConProductosResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteRequest;
import ar.edu.utn.diplomatura.customerservice.dto.ClienteResponse;
import ar.edu.utn.diplomatura.customerservice.dto.ErrorResponse;
import ar.edu.utn.diplomatura.customerservice.servicios.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 * endpoints de Cliente. Requieren token: GET para cualquier rol, POST y PUT solo ADMIN.
 */
@Tag(name = "Clientes")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes (puede estar vacia)")
    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listarTodos();
    }

    @Operation(summary = "Buscar cliente por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@Parameter(description = "Id del cliente", example = "1") @PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @Operation(summary = "Buscar cliente por CUIT/CUIL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese CUIT/CUIL",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cuit/{cuit}")
    public ClienteResponse buscarPorCuit(
            @Parameter(description = "CUIT/CUIL sin guiones", example = "20450703398") @PathVariable String cuit) {
        return clienteService.buscarPorCuit(cuit);
    }

    @Operation(summary = "Cliente con sus productos",
            description = "Trae los productos desde product-service usando Feign + Eureka.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente y sus productos"),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "product-service no esta disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/productos")
    public ClienteConProductosResponse buscarConProductos(
            @Parameter(description = "Id del cliente", example = "1") @PathVariable Long id) {
        return clienteService.buscarConProductos(id);
    }

    @Operation(summary = "Registrar cliente (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Falta algun dato, el CUIT/CUIL no tiene 11 digitos o el email no es valido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un cliente con ese CUIT/CUIL o email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return clienteService.crear(request);
    }

    @Operation(summary = "Actualizar cliente (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El CUIT/CUIL o email nuevo ya esta usado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ClienteResponse actualizar(@Parameter(description = "Id del cliente", example = "3") @PathVariable Long id,
                                      @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }
}
