package com.apiestudar.api_prodify.interfaces.controller;

import java.time.Duration;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.application.TokenService;
import com.apiestudar.api_prodify.application.usecase.auth.RealizarLoginUseCase;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    @Autowired
	private RealizarLoginUseCase realizarLogin;
	@Autowired
	private ModelMapper modelMapper;

    @Operation(summary = "Realiza um login com auntenticação JWT.", description = "Realiza uma operação de login com autenticação de token via Spring Security - JWT e com senha criptografada.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login realizado.")
	})
    @PostMapping("/realizarLogin")
    public ResponseEntity<?> realizarLogin(@RequestBody UsuarioDTO cred, jakarta.servlet.http.HttpServletResponse res) {
        UsuarioDTO usuario = realizarLogin.executar(cred);
		Usuario usuarioClass = modelMapper.map(usuario, Usuario.class);
        String accessToken = tokenService.gerarAccessToken(usuarioClass);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/auth/refresh")
            .maxAge(Duration.ofDays(15))
            .build();

        res.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "usuario", usuario
        ));
    }

    @Operation(summary = "Realiza um refresh do token JWT", description = "Realiza uma operação de atualizar o token JWT para que o usuário continue logado sem a necessidade de fazer login novamente.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Refresh executado com sucesso.")
	})
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refresh_token") String refreshToken,
                                     HttpServletResponse res) {
        String subject = tokenService.validarEExtrairSubjectDoRefresh(refreshToken);
        String novoAccess = tokenService.gerarAccessTokenPorSubject(subject);
        return ResponseEntity.ok(Map.of("accessToken", novoAccess));
    }

    @Operation(summary = "Realiza logout do usuário", description = "Realiza uma operação de logout do usuário do sistema, removendo o token JWT do usuário.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Logout executado com sucesso.")
	})
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        ResponseCookie expired = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/api/auth/refresh")
            .maxAge(0)
            .build();
        res.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
        return ResponseEntity.noContent().build();
    }
}


