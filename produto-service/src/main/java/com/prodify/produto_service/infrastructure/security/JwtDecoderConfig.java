package com.prodify.produto_service.infrastructure.security;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    private static final String SECRET_KEY = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withSecretKey(new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256"))
            .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("AppProdify"));
        return decoder;
    }
}
