package com.apiestudar.api_prodify.application.usecase.usuario;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.application.TokenService;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class RealizarLoginUseCase {

	private final TokenService tokenService;
    private final UsuarioHelper usuarioHelper;

    @Autowired
    public RealizarLoginUseCase(UsuarioHelper usuarioHelper, TokenService tokenService) {
        this.usuarioHelper = usuarioHelper;
        this.tokenService = tokenService;
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executar(Usuario usuario) {
        Helper.verificarNull(usuario);

        BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();
        String senhaArmazenada = usuarioHelper.getSenhaByLogin(usuario.getLogin());
        Map<String, Object> response = new HashMap<>();

        if (senhaCriptografada.matches(usuario.getSenha(), senhaArmazenada)) {
            Usuario usuarioLogado = usuarioHelper.findByLogin(usuario.getLogin());
            String token = tokenService.gerarToken(usuario);
            usuarioLogado.setToken(token);
            response.put("usuario", usuarioLogado);
        } else {
            response.put("msgCredenciaisInvalidas", "Credenciais inválidas");
        }

        return response;
    }
    
}