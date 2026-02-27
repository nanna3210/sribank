package com.sribank.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sri Bank Auth Service API")
                        .description("Authentication and authorization APIs for Sri Bank")
                        .version("v1")
                        .contact(new Contact().name("Sri Bank Engineering"))
                        .license(new License().name("Internal Use Only")));
    }
}
