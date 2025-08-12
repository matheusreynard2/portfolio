package com.apiestudar.api_prodify.application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {

    private static final String JWT_SECRET = "VGhpc0lzQVNlY3VyZVNlY3JldEtleTIwMjU=";
    private static final String ISSUER = "AppProdify";

    public String gerarAccessToken(Usuario usuario) {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(usuario.getLogin())
            .withClaim("id", usuario.getIdUsuario())
            .withClaim("type", "access")
            .withExpiresAt(Date.from(LocalDateTime.now()
                .plusMinutes(3)
                .toInstant(ZoneOffset.of("-03:00"))))
            .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    public String gerarAccessTokenPorSubject(String subject) {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(subject)
            .withClaim("type", "access")
            .withExpiresAt(Date.from(LocalDateTime.now()
                .plusMinutes(15)
                .toInstant(ZoneOffset.of("-03:00"))))
            .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    public String gerarRefreshToken(UsuarioDTO usuario) {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(usuario.getLogin())
            .withClaim("id", usuario.getIdUsuario())
            .withClaim("type", "refresh")
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(Date.from(LocalDateTime.now()
                .plusDays(15)
                .toInstant(ZoneOffset.of("-03:00"))))
            .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    public String validarEExtrairSubjectDoRefresh(String refreshToken) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(JWT_SECRET))
            .withIssuer(ISSUER)
            .build()
            .verify(refreshToken);

        String type = decoded.getClaim("type").asString();
        if (!"refresh".equals(type)) {
            throw new IllegalArgumentException("Token inválido para refresh");
        }
        return decoded.getSubject();
    }

    // Opcional
    public String rotacionarRefresh(String oldRefresh) {
        DecodedJWT decoded = JWT.decode(oldRefresh);
        String subject = decoded.getSubject();
        Long id = decoded.getClaim("id").asLong();
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setLogin(subject);
        usuario.setIdUsuario(id != null ? id : 0L);
        return gerarRefreshToken(usuario);
    }
}