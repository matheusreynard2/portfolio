package com.apiestudar.api_prodify.application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenService {
	
	// Secret JWT - DEVE SER O MESMO EM TODOS OS MICROSSERVIÇOS
	private static final String JWT_SECRET = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";
	
	public String gerarToken(Usuario usuario) {
		return JWT.create()
				.withIssuer("AppProdify")                          // quem emitiu
				.withSubject(usuario.getLogin())                  // quem é o dono do token
				.withClaim("id", usuario.getIdUsuario())          // ID do usuário
				.withExpiresAt(Date.from(LocalDateTime.now()
										.plusMinutes(1)
										.toInstant(ZoneOffset.of("-03:00"))))  // expiração correta
				.sign(Algorithm.HMAC256(JWT_SECRET));             // assinatura HMAC com segredo
	}

	// RECUPERAR O TOKEN
	public Object getSubject(String token) {
		return JWT.require(Algorithm.HMAC256(JWT_SECRET))
				.withIssuer("AppProdify")
				.build().verify(token).getSubject();
	}

}