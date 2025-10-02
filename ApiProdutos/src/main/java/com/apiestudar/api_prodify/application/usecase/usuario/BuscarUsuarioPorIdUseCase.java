package com.apiestudar.api_prodify.application.usecase.usuario;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.UsuarioDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class BuscarUsuarioPorIdUseCase {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public BuscarUsuarioPorIdUseCase(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioDTO executar(long id) {
        long t0 = System.nanoTime();
        if (usuarioRepository.buscarUsuarioPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        } else {
            UsuarioDTO usuarioDTO = modelMapper.map(usuarioRepository.buscarUsuarioPorId(id).get(), UsuarioDTO.class);
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### BUSCAR USUARIO %d ns ( %d ms)%n", ns, ns / 1_000_000);
            System.out.println("##############################");
            return usuarioDTO;
        }
    }
}
