package com.prodify.api_gateway;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import reactor.core.publisher.Mono;

@Component
public class GlobalTokenRelayFilter implements GlobalFilter, Ordered {

    // Secret JWT - DEVE SER O MESMO EM TODOS OS MICROSSERVIÇOS
    private static final String JWT_SECRET = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/api/usuarios/addNovoAcessoIp",
        "/api/usuarios/getAllAcessosIp",
        "/api/usuarios/realizarLogin",
        "/api/usuarios/adicionarUsuario"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Ignorar autenticação para endpoints públicos
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            System.out.println("[API Gateway] Endpoint público: " + path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println("[API Gateway] Path: " + path);
        System.out.println("[API Gateway] Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            System.out.println("[API Gateway] Token extraído: " + token);

            try {
                // Primeiro, vamos decodificar o token sem verificar para ver o conteúdo
                DecodedJWT decodedJWT = JWT.decode(token);
                System.out.println("[API Gateway] Token Header: " + decodedJWT.getHeader());
                System.out.println("[API Gateway] Token Payload: " + decodedJWT.getPayload());
                System.out.println("[API Gateway] Token Issuer: " + decodedJWT.getIssuer());
                System.out.println("[API Gateway] Token Subject: " + decodedJWT.getSubject());
                System.out.println("[API Gateway] Token Expires At: " + decodedJWT.getExpiresAt());

                // Verificar se o token tem a estrutura correta
                if (!"AppProdify".equals(decodedJWT.getIssuer())) {
                    System.err.println("[API Gateway] ❌ Token com issuer incorreto: " + decodedJWT.getIssuer());
                    return createErrorResponse(exchange, "Token com issuer incorreto");
                }

                // Agora vamos validar o token com o secret correto
                System.out.println("[API Gateway] Validando token com secret: " + JWT_SECRET);
                System.out.println("[API Gateway] Secret decodificado: " + new String(Base64.getDecoder().decode(JWT_SECRET)));
                
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withIssuer("AppProdify")
                    .build()
                    .verify(token);

                System.out.println("[API Gateway] ✅ Token válido! Repassando para o serviço...");

                // Repassa o token
                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .build();

                return chain.filter(exchange.mutate().request(mutated).build());

            } catch (TokenExpiredException e) {
                System.err.println("[API Gateway] ❌ Token expirado: " + e.getMessage());
                // Estrutura de erro esperada pelo frontend
                String body = "{\"status\": 401, \"error\": {\"message\": \"Tempo limite de conexão com o sistema excedido. TOKEN Expirado\"}}";

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes)));
            } catch (SignatureVerificationException e) {
                System.err.println("[API Gateway] ❌ ASSINATURA INVÁLIDA - Token foi gerado com secret diferente!");
                System.err.println("[API Gateway] Erro: " + e.getMessage());
                System.err.println("[API Gateway] Isso indica que o token foi gerado com um secret diferente do atual.");
                System.err.println("[API Gateway] Sugestão: Limpar localStorage do frontend e fazer novo login.");
                
                // Retornar erro específico para assinatura inválida
                String body = "{\"status\": 401, \"error\": {\"message\": \"Tempo limite de conexão com o sistema excedido. TOKEN Expirado\"}}";
                
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes)));
            } catch (Exception e) {
                // Log do erro para debug
                System.err.println("[API Gateway] ❌ Erro na validação do token: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                
                // Estrutura de erro padronizada para token inválido
                String body = "{\"status\": 401, \"error\": {\"message\": \"Token inválido\"}}";
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes)));
            }
        }

        System.out.println("[API Gateway] Nenhum token encontrado, seguindo para o serviço...");
        // Se não tiver token, apenas segue a requisição (validação fica nos serviços)
        return chain.filter(exchange);
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, String message) {
        String body = "{\"status\": 401, \"error\": {\"message\": \"" + message + "\"}}";
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridade
    }
}
