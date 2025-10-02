package com.apiestudar.api_prodify.application.usecase.usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public DeletarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean executar(long id) {
        long t0 = System.nanoTime();
        if (usuarioRepository.buscarUsuarioPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        } else {
            usuarioRepository.deletarUsuario(id);
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### DELETAR USUARIO %d ns ( %d ms)%n", ns, ns / 1_000_000);
            System.out.println("##############################");
            return true;
        }
    }
}
