
package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;

@Service
public class ListarFornecedoresUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public ListarFornecedoresUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

	public Page<Fornecedor> executar(Pageable pageable) {
		return fornecedorRepository.listarFornecedores(pageable);
	}
}
