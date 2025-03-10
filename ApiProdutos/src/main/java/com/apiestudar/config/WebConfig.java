package com.apiestudar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Aplica a configuração a todos os endpoints /api/*
                .allowedOrigins("http://localhost:4200", "http://localhost:8080", "http://www.sistemaprodify.com", "http://www.sistemaprodify.com:8080", "http://www.sistemaprodify.com:80", "http://191.252.38.22:8080", "http://191.252.38.22:80", "http://191.252.38.22")  // Permite qualquer origem. Pode ser restringido para domínios específicos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // Tempo de cache para o preflight (OPTIONS)
    }

}
