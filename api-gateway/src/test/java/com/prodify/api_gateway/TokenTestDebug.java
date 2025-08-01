package com.prodify.api_gateway;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class TokenTestDebug {

    @Test
    public void testJWTSecret() {
        String secret = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";
        
        // Decodificar o secret base64
        byte[] decodedSecret = Base64.getDecoder().decode(secret);
        String decodedSecretString = new String(decodedSecret);
        
        System.out.println("Secret original: " + secret);
        System.out.println("Secret decodificado: " + decodedSecretString);
        
        // Criar um algoritmo para testar
        Algorithm algorithm = Algorithm.HMAC256(secret);
        System.out.println("Algoritmo criado com sucesso: " + algorithm.getName());
        
        // Criar um token de teste
        String testToken = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 1)
                .sign(algorithm);
        
        System.out.println("Token de teste criado: " + testToken);
        
        // Verificar o token
        try {
            JWT.require(algorithm)
                .withIssuer("AppProdify")
                .build()
                .verify(testToken);
            System.out.println("Token verificado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao verificar token: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 