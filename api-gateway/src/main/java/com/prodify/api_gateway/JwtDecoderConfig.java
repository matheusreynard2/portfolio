package com.prodify.api_gateway;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    private static final String SECRET_KEY = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
        .withSecretKey(new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256"))
        .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("AppProdify"));
        return decoder;
    }
}
