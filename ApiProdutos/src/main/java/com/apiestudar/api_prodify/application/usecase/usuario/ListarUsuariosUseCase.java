package com.apiestudar.api_prodify.application.usecase.usuario;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Usuario;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class ListarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UsuarioDTO> executar() {
        long t0 = System.nanoTime();
        List<Usuario> usuarios = usuarioRepository.listarUsuarios();
        List<UsuarioDTO> usuarioDTList = Helper.mapClassToDTOList(usuarios, UsuarioDTO.class);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### LISTAR USUARIOS %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return usuarioDTList;
    }
}
