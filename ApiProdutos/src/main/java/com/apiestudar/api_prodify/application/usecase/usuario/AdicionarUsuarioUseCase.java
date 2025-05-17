
package com.apiestudar.api_prodify.application.usecase.usuario;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHelper usuarioHelper;
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    @Autowired
    public AdicionarUsuarioUseCase(UsuarioRepository usuarioRepository, UsuarioHelper usuarioHelper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHelper = usuarioHelper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object executar(String usuarioJSON, MultipartFile imagemFile) throws IOException {
        Helper.verificarNull(usuarioJSON);

        if (usuarioHelper.contarLoginRepetido(usuarioJSON) >= MAX_NUMBER_REGISTERED_LOGIN) {
            return "Login já cadastrado no banco de dados.";
        } else {
            Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
            user.setImagem(imagemFile.getBytes());
            String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
            user.setSenha(senhaCriptografada);
            return usuarioRepository.adicionarUsuario(user);
        }
    }

}
