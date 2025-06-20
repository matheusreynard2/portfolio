package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class BuscarFornecedorUseCase {

    @Autowired
    private ModelMapper modelMapper;
    private final FornecedorRepository fornecedorRepository;

    public BuscarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public FornecedorDTO executar(Long id) {
        Fornecedor fornecedor = fornecedorRepository.buscarFornecedorPorId(id)
            .orElseThrow(RegistroNaoEncontradoException::new);  
        return modelMapper.map(fornecedor, FornecedorDTO.class);
    }
} 