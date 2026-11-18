package ar.edu.utn.diplomatura.customerservice.integracion;

import ar.edu.utn.diplomatura.customerservice.dto.ProductoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente Feign hacia product-service.
 * No hay URL fija: "product-service" es el nombre registrado en Eureka y el
 * LoadBalancer resuelve a que instancia llamar.
 */
@FeignClient(name = "product-service")
public interface ProductoClient {

    @GetMapping("/productos/cliente/{clienteId}")
    List<ProductoResponse> productosDelCliente(@PathVariable("clienteId") Long clienteId);
}
