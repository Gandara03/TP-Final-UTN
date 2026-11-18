package ar.edu.utn.diplomatura.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion Swagger / OpenAPI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("product-service")
                        .version("1.0.0")
                        .description("""
                                Microservicio de productos financieros: cuentas, tarjetas, prestamos \
                                e inversiones de cada cliente.

                                Trabajo practico final"""))
                .tags(List.of(
                        new Tag().name("Productos").description("ABM de productos y consulta por cliente")));
    }
}
