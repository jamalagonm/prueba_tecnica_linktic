package com.microservicio.inventario.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Inventario API")
                        .description("API REST para gestión de productos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("prueba linktic")
                                .email("dev@microservicio.com")))
                .addSecurityItem(new SecurityRequirement().addList("API-KEY"))
                .components(new Components()
                        .addSecuritySchemes("API-KEY",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-KEY")
                                        .description("API Key de autenticación")));
    }
}
