package com.apiestudar.api_prodify.application.usecase.usuario;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioFormDTO;
import com.apiestudar.api_prodify.shared.exception.LoginJaExisteException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHelper usuarioHelper;
    private final ModelMapper modelMapper;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    public AdicionarUsuarioUseCase(UsuarioRepository usuarioRepository, UsuarioHelper usuarioHelper, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHelper = usuarioHelper;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioDTO executar(UsuarioFormDTO usuarioFormDTO, MultipartFile imagemFile) throws IOException {
            
        Helper.verificarNull(usuarioFormDTO);
        Helper.verificarNull(imagemFile);
        UsuarioDTO usuarioDTO = objectMapper.readValue(usuarioFormDTO.getUsuarioJson(), UsuarioDTO.class);
		Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);	
        
        if (usuarioHelper.contarLoginRepetido(usuario.getLogin()) >= MAX_NUMBER_REGISTERED_LOGIN) {
            throw new LoginJaExisteException();
        } else {
            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
            usuario.setImagem(imagemFile.getBytes());
            usuario = usuarioRepository.adicionarUsuario(usuario);
            usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);
            return usuarioDTO;
        }
    }

}
