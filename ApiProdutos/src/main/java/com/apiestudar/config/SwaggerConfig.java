package com.apiestudar.config;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	
	// URL SWAGGER: http://localhost:8080/swagger-ui/index.html

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.apiestudar"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
    
    // Personalize as informações da API, incluindo título, descrição, versão, etc.
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("PRODIFY API")  // Define o título da API
                .description("API do sistema de gerenciamento de proutos Prodify")  // Descrição da API
                .version("1.0.0")  // Versão da API
                .build();
    }
    
    @Bean
    public GroupedOpenApi usuario() {
        return GroupedOpenApi.builder()
                .group("Usuário endpoints")  // Nome do grupo que aparecerá no Swagger UI
                .pathsToMatch("api/usuarios/**") // Define que os endpoints com /usuarios/** serão do grupo "User operations"
                .build();
    }
    
    @Bean
    public GroupedOpenApi produto() {
        return GroupedOpenApi.builder()
                .group("Produto endpoints")  // Nome do grupo para os produtos
                .pathsToMatch("api/produtos/**") // Define que os endpoints com /produtos/** serão do grupo "Product operations"
                .build();
    }
    
}