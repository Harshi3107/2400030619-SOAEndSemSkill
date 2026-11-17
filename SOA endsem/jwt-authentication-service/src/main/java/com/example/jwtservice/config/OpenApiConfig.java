package com.example.jwtservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jwtAuthenticationOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("JWT Authentication Service API")
                        .description("Educational API for issuing signed JWTs from an already-authenticated user payload.")
                        .version("1.0.0"));
    }
}
