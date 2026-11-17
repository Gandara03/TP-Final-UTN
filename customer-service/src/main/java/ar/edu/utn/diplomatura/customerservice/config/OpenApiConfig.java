package ar.edu.utn.diplomatura.customerservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion Swagger / OpenAPI. Agrega el boton "Authorize" para pegar el token de la sesion.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("customer-service")
                        .version("1.0.0")
                        .description("""
                                Microservicio de clientes. Consulta los productos de cada cliente \
                                a product-service con Feign.

                                Primero hacer login en /auth/login y pegar el token en Authorize.

                                Trabajo practico final"""))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .tags(List.of(
                        new Tag().name("Autenticacion").description("Login con JWT"),
                        new Tag().name("Clientes").description("Alta, consulta y modificacion de clientes y sus productos")));
    }
}
