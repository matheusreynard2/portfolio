package com.apiestudar.api_prodify.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    // URL SWAGGER: http://localhost:8080/swagger-ui.html/index.html
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host("localhost:8080")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.apiestudar"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
    
    // Personalize as informações da API
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("PRODIFY API")
                .description("API do sistema de gerenciamento de produtos Prodify")
                .version("1.0.0")
                .build();
    }
    
    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder
            .builder()
            .defaultModelsExpandDepth(-1)
            .build();
    }
} 