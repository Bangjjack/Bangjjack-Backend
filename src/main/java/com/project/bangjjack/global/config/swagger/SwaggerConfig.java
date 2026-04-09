package com.project.bangjjack.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {

    private static final String JWT_SCHEME = "jwtAuth";

    @Value("${server-uri}")
    private String serverUri;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server().url(serverUri);
        Server localServer = new Server().url("/");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME))
                .components(
                        new Components().addSecuritySchemes(
                                JWT_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                )
                .servers(List.of(localServer, server))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Bangjjack")
                .description("Bangjjack API 문서")
                .version("1.1.0");
    }
}
