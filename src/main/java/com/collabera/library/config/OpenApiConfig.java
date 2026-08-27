package com.collabera.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI libraryOpenApi() {
        return new OpenAPI().info(new Info().title("Library System API").version("v1")
                .description("Register borrowers and book copies, then borrow and return individual copies."));
    }
}
