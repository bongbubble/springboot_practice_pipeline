package com.skala.springbootpractice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Centralized OpenAPI definition so Swagger UI exposes basic metadata
 * and automatically picks up existing controllers.
 */
@Configuration
public class OpenApiConfig {

    @Value("${springdoc.server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI httpRequestJpaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Http Request JPA Sample API")
                        .description("Sample Spring Boot service instrumented for Jenkins + ArgoCD pipeline")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://example.com/docs"))
                .servers(List.of(new Server().url(serverUrl)));
    }
}
