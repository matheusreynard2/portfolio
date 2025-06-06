package com.apiestudar.api_prodify.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.apiestudar.api_prodify.infrastructure.security.FilterToken;

@Component
@Profile("test")
public class TestFilterToken extends FilterToken {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Não faz nenhuma validação, apenas passa a requisição adiante
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Sempre retorna true para não filtrar nenhuma requisição
        return true;
    }
} 