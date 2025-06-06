package com.apiestudar.api_prodify.application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenService {
	
	// GERAR O TOKEN
	public String gerarToken(Usuario usuario) {	
		return JWT.create()
				.withIssuer("AppProdify")
				.withSubject(usuario.getLogin())
				.withClaim("id", usuario.getIdUsuario())
			    .withExpiresAt(LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00")))
			    .sign(Algorithm.HMAC256("secret"));
			    
	}

	// RECUPERAR O TOKEN
	public Object getSubject(String token) {
		return JWT.require(Algorithm.HMAC256("secret"))
				.withIssuer("AppProdify")
				.build().verify(token).getSubject();
	}

}