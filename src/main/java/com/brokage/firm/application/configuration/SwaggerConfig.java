package com.brokage.firm.application.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Live Betting API")
                        .description("API documentation for brokage firm operations")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Server")
                ));
    }

    @Bean
    public GroupedOpenApi orderAndAssetApi() {
        return GroupedOpenApi.builder()
                .group("controllers")
                .pathsToMatch("/api/orders/**", "/api/assets/**")
                .build();
    }
}
