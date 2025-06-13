package com.apiestudar.api_prodify.application.usecase.usuario;

import java.io.IOException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.mapper.UsuarioMapper;
import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class AdicionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHelper usuarioHelper;
    private final UsuarioMapper usuarioMapper;
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    public AdicionarUsuarioUseCase(UsuarioRepository usuarioRepository, UsuarioHelper usuarioHelper, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHelper = usuarioHelper;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object executar(UsuarioDTO usuarioDTO, MultipartFile imagemFile) throws IOException {
            
        Helper.verificarNull(usuarioDTO);
        Usuario user = usuarioMapper.toEntity(usuarioDTO);
        
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
