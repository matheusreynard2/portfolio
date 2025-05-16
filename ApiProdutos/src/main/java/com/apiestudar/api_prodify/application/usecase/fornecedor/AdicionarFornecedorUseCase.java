
package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class AdicionarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public AdicionarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public Fornecedor executar(Fornecedor fornecedor) {
        Helper.verificarNull(fornecedor);
        return fornecedorRepository.adicionarFornecedor(fornecedor);
    }
}
