package com.apiestudar.api_prodify.infrastructure.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestDTOArgumentResolver requestDTOArgumentResolver;

    public WebConfig(RequestDTOArgumentResolver requestDTOArgumentResolver) {
        this.requestDTOArgumentResolver = requestDTOArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(requestDTOArgumentResolver);
    }
}
