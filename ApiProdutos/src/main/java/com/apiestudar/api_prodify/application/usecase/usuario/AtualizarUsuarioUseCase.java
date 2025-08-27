package com.apiestudar.api_prodify.application.usecase.usuario;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioFormDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    public UsuarioDTO executar(Long idUsuario, UsuarioFormDTO usuarioFormDTO, MultipartFile imagemFile) throws IOException {
        
        Helper.verificarNull(usuarioFormDTO);
        Helper.verificarNull(idUsuario);
        Helper.maiorZero(idUsuario);

        UsuarioDTO usuarioDTO = objectMapper.readValue(usuarioFormDTO.getUsuarioJson(), UsuarioDTO.class);
        Usuario usuarioExistente = usuarioRepository.buscarUsuarioPorId(idUsuario)
        .orElseThrow(() -> new RegistroNaoEncontradoException());

         usuarioExistente.setEmail(usuarioDTO.getEmail());

        // Atualiza senha SOMENTE se foi informada (não vazia); criptografa a nova senha
        if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().trim().isEmpty()) {
            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuarioDTO.getSenha());
            usuarioExistente.setSenha(senhaCriptografada);
        }

        usuarioExistente.setSaldo(usuarioDTO.getSaldo());

        if (imagemFile != null) {
            usuarioExistente.setImagem(imagemFile.getBytes());
        } else if (imagemFile == null) {
            usuarioExistente.setImagem(usuarioDTO.getImagem());
        }
        
        usuarioRepository.atualizarUsuario(usuarioExistente);
        
        // JPA detecta mudanças automaticamente - nem precisa de save()!
        return modelMapper.map(usuarioExistente, UsuarioDTO.class);
    }
 }