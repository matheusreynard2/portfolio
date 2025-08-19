package com.prodify.api_gateway;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain
;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import reactor.core.publisher.Mono;

@Component
public class GlobalTokenRelayFilter implements GlobalFilter, Ordered {

    private static final String JWT_SECRET = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/api/usuarios/addNovoAcessoIp",
        "/api/usuarios/getAllAcessosIp",
        "/api/usuarios/adicionarUsuario",
        "/api/auth/realizarLogin",
        "/api/auth/refresh",
        "/api/auth/logout",
        // Swagger / OpenAPI endpoints
        "/swagger-ui",
        "/swagger-ui.html",
        "/v2/api-docs",
        "/v3/api-docs",
        "/swagger-resources",
        "/webjars"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Ignorar autenticação para endpoints públicos
        if (PUBLIC_ENDPOINTS.stream().anyMatch(pub -> path.equals(pub) || path.startsWith(pub + "/") || path.startsWith(pub))) {
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
                DecodedJWT decodedJWT = JWT.decode(token);

                if (!"AppProdify".equals(decodedJWT.getIssuer())) {
                    System.err.println("[API Gateway] Token com issuer incorreto: " + decodedJWT.getIssuer());
                    return createErrorResponse(exchange, "Token com issuer incorreto", 401);
                }

                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withIssuer("AppProdify")
                    .build()
                    .verify(token);

                System.out.println("[API Gateway] Token válido! Repassando para o serviço...");

                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .build();

                return chain.filter(exchange.mutate().request(mutated).build());

            } catch (TokenExpiredException e) {
                System.err.println("[API Gateway] Token expirado: " + e.getMessage());
                return createErrorResponse(exchange, "Tempo limite de conexão com o sistema excedido. TOKEN Expirado", 440);

            } catch (SignatureVerificationException e) {
                return createErrorResponse(exchange, "Token com assinatura inválida", 401);

            } catch (Exception e) {
                System.err.println("[API Gateway] Erro na validação do token: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                return createErrorResponse(exchange, "Token inválido", 401);
            }
        }

        // Se chegou aqui, não tinha Authorization header ou não era Bearer
        System.err.println("[API Gateway] Requisição sem token válido para endpoint protegido: " + path);
        return createErrorResponse(exchange, "Tempo limite de conexão com o sistema excedido. TOKEN Expirado", 440);
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, String message, int statusCode) {
        int httpStatus = (statusCode == 440) ? 440 : 401;
        String body = "{\"status\": " + httpStatus + ", \"error\": {\"message\": \"" + message + "\"}}";
        if (statusCode == 440) {
            exchange.getResponse().setRawStatusCode(440);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridade
    }
}
