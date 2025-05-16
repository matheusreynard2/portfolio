package com.apiestudar.api_prodify.application.usecase.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DeletarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean executar(long id) {
        if (usuarioRepository.buscarUsuarioPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        } else {
            usuarioRepository.deletarUsuario(id);
            return true;
        }
    }
}
