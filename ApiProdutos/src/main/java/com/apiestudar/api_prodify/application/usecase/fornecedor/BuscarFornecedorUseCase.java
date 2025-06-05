package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class BuscarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public BuscarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public Fornecedor executar(Long id) {
        return fornecedorRepository.buscarFornecedorPorId(id)
            .orElseThrow(RegistroNaoEncontradoException::new);
    }
} 