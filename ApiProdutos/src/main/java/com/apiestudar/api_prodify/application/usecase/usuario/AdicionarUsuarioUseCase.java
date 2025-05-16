
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private static final int MAX_NUMBER_REGISTERED_LOGIN = 1;

    @Autowired
    public AdicionarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object executar(String usuarioJSON, MultipartFile imagemFile) throws IOException {
        Helper.verificarNull(usuarioJSON);

        if (findLoginRepetido(usuarioJSON) >= MAX_NUMBER_REGISTERED_LOGIN) {
            return "Login já cadastrado no banco de dados.";
        } else {
            Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
            user.setImagem(imagemFile.getBytes());
            String senhaCriptografada = new BCryptPasswordEncoder().encode(user.getSenha());
            user.setSenha(senhaCriptografada);
            return usuarioRepository.adicionarUsuario(user);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public int findLoginRepetido(String usuarioJSON) throws JsonProcessingException {
        Usuario user = new ObjectMapper().readValue(usuarioJSON, Usuario.class);
        return usuarioRepository.contarLoginRepetido(user.getLogin());
    }
}
