package com.apiestudar.api_prodify.application.usecase.auth;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.application.TokenService;
import com.apiestudar.api_prodify.application.usecase.usuario.UsuarioHelper;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.shared.exception.CredenciaisInvalidasException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class RealizarLoginUseCase {

	private final TokenService tokenService;
    private final UsuarioHelper usuarioHelper;
    private final ModelMapper modelMapper;

    public RealizarLoginUseCase(UsuarioHelper usuarioHelper, TokenService tokenService, ModelMapper modelMapper) {
        this.usuarioHelper = usuarioHelper;
        this.tokenService = tokenService;
        this.modelMapper = modelMapper;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public UsuarioDTO executar(UsuarioDTO usuarioDTO) {
        Helper.verificarNull(usuarioDTO);
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        BCryptPasswordEncoder senhaCriptografada = new BCryptPasswordEncoder();
        String senhaArmazenada = usuarioHelper.getSenhaByLogin(usuario.getLogin());

        if (senhaCriptografada.matches(usuario.getSenha(), senhaArmazenada)) {
            Usuario usuarioLogado = usuarioHelper.findByLogin(usuario.getLogin());
            String token = tokenService.gerarAccessToken(usuario);
            usuarioLogado.setToken(token);
            return modelMapper.map(usuarioLogado, UsuarioDTO.class);
        } else {
            throw new CredenciaisInvalidasException();
        }
    }
}