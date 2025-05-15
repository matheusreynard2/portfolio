package com.apiestudar.api_prodify.infrastructure.security;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.apiestudar.api_prodify.application.TokenService;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.auth0.jwt.exceptions.TokenExpiredException;

// FILTRO DO TOKEN PARA O AUTHCONFIGURATIONS
@Component
public class FilterToken implements Filter {

	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token;
		var httpRequest = (HttpServletRequest) request;
		var authorizationHeader = httpRequest.getHeader("Authorization");
		
		if (authorizationHeader != null) {
			token = authorizationHeader.replace("Bearer ", "");

			try {
				
				var subject = this.tokenService.getSubject(token);
				
				var usuario = this.usuarioRepository.buscarPorLogin(subject.toString());
				
				var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			} catch (TokenExpiredException e) {
				// Se o token expirou, enviamos a resposta com o código 401 e uma mensagem customizada
               ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Tempo limite de conexão com o sistema excedido. TOKEN Expirado\"}");
                return;  // Interrompe a execução e não passa para o próximo filtro
			}
		}
		// Chame o próximo filtro na cadeia
		chain.doFilter(request, response);
	}
}