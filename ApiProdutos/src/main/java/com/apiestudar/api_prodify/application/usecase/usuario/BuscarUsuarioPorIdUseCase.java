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
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public BuscarUsuarioPorIdUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioDTO executar(long id) {
        if (usuarioRepository.buscarUsuarioPorId(id).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        } else {
            return modelMapper.map(usuarioRepository.buscarUsuarioPorId(id).get(), UsuarioDTO.class);
        }
    }
}
