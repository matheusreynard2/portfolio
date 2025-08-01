package com.prodify.produto_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.GrantedAuthority;

//import com.prodify.produto_service.infrastructure.security.GrantedAuthorityDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Suporte para Optional, Stream, etc
        mapper.registerModule(new Jdk8Module());

        // Suporte para Java 8 Time API (LocalDateTime, etc)
        mapper.registerModule(new JavaTimeModule());

        // Configurações para evitar loops infinitos e dependências circulares
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        mapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);

        // Registra deserializador customizado para GrantedAuthority
        //SimpleModule module = new SimpleModule();
        ////dule.addDeserializer(GrantedAuthority.class, new GrantedAuthorityDeserializer());
        //mapper.registerModule(module);

        return mapper;
    }
} 