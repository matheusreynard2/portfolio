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
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class AdicionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHelper usuarioHelper;
    private final ModelMapper modelMapper;
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    public AdicionarUsuarioUseCase(UsuarioRepository usuarioRepository, UsuarioHelper usuarioHelper, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHelper = usuarioHelper;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object executar(UsuarioDTO usuarioDTO, MultipartFile imagemFile) throws IOException {
            
        Helper.verificarNull(usuarioDTO);
        Usuario user = modelMapper.map(usuarioDTO, Usuario.class);
        
        if (usuarioHelper.contarLoginRepetido(user.getLogin()) >= MAX_NUMBER_REGISTERED_LOGIN) {
            return "Login já cadastrado no banco de dados.";
        } else {
            // Só define a imagem se ela não for nula
            if (imagemFile != null && !imagemFile.isEmpty()) {
                user.setImagem(imagemFile.getBytes());
            }
            String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
            user.setSenha(senhaCriptografada);
            return usuarioRepository.adicionarUsuario(user);
        }
    }

}
