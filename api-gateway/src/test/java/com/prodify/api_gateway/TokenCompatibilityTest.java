package com.prodify.api_gateway;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class TokenCompatibilityTest {

    @Test
    public void testTokenCompatibilityWithApiProdutos() {
        String secret = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";
        
        // Simular exatamente como o ApiProdutos gera o token
        String tokenFromApiProdutos = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
        
        System.out.println("Token gerado como ApiProdutos: " + tokenFromApiProdutos);
        
        // Tentar verificar o token como faz o Gateway
        try {
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("AppProdify")
                .build()
                .verify(tokenFromApiProdutos);
            System.out.println("✅ Token verificado com sucesso no Gateway!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar token no Gateway: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        
        // Verificar se o problema é com o timezone
        System.out.println("Timezone atual: " + ZoneId.systemDefault());
        System.out.println("Timezone usado no token: " + ZoneOffset.of("-03:00"));
        
        // Testar com timezone do sistema
        String tokenWithSystemTimezone = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .sign(Algorithm.HMAC256(secret));
        
        System.out.println("Token com timezone do sistema: " + tokenWithSystemTimezone);
        
        try {
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("AppProdify")
                .build()
                .verify(tokenWithSystemTimezone);
            System.out.println("✅ Token com timezone do sistema verificado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar token com timezone do sistema: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
} 