package com.prodify.api_gateway;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenAnalysisTest {

    @Test
    public void analyzeTokenScenarios() {
        String secret = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";
        
        System.out.println("=== ANÁLISE DE CENÁRIOS DE TOKEN ===");
        
        // Cenário 1: Token válido
        String validToken = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
        
        System.out.println("\n1. Token válido: " + validToken);
        testTokenValidation(validToken, secret);
        
        // Cenário 2: Token com secret diferente
        String wrongSecretToken = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256("secret"));
        
        System.out.println("\n2. Token com secret diferente: " + wrongSecretToken);
        testTokenValidation(wrongSecretToken, secret);
        
        // Cenário 3: Token expirado
        String expiredToken = JWT.create()
                .withIssuer("AppProdify")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().minusMinutes(1).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
        
        System.out.println("\n3. Token expirado: " + expiredToken);
        testTokenValidation(expiredToken, secret);
        
        // Cenário 4: Token com issuer diferente
        String wrongIssuerToken = JWT.create()
                .withIssuer("WrongIssuer")
                .withSubject("testUser")
                .withClaim("id", 138)
                .withExpiresAt(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
        
        System.out.println("\n4. Token com issuer diferente: " + wrongIssuerToken);
        testTokenValidation(wrongIssuerToken, secret);
        
        // Cenário 5: Token malformado
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.INVALID_PAYLOAD.INVALID_SIGNATURE";
        
        System.out.println("\n5. Token malformado: " + malformedToken);
        testTokenValidation(malformedToken, secret);
        
        // Cenário 6: Token com espaços ou caracteres especiais
        String tokenWithSpaces = " " + validToken + " ";
        
        System.out.println("\n6. Token com espaços: '" + tokenWithSpaces + "'");
        testTokenValidation(tokenWithSpaces.trim(), secret);
        
        // Cenário 7: Verificar se o secret está sendo usado corretamente
        System.out.println("\n=== ANÁLISE DO SECRET ===");
        System.out.println("Secret original: " + secret);
        System.out.println("Secret decodificado: " + new String(Base64.getDecoder().decode(secret)));
        System.out.println("Secret length: " + secret.length());
        System.out.println("Secret decoded length: " + new String(Base64.getDecoder().decode(secret)).length());
    }
    
    private void testTokenValidation(String token, String secret) {
        try {
            // Primeiro, tentar decodificar sem verificar
            DecodedJWT decodedJWT = JWT.decode(token);
            System.out.println("   - Token decodificado com sucesso");
            System.out.println("   - Issuer: " + decodedJWT.getIssuer());
            System.out.println("   - Subject: " + decodedJWT.getSubject());
            System.out.println("   - Expires At: " + decodedJWT.getExpiresAt());
            
            // Agora tentar verificar
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("AppProdify")
                .build()
                .verify(token);
            System.out.println("   - ✅ Token VÁLIDO");
            
        } catch (Exception e) {
            System.out.println("   - ❌ Token INVÁLIDO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
} 