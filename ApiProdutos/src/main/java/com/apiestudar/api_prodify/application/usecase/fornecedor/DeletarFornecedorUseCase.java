
package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public DeletarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

	public boolean executar(long id) {
		if (fornecedorRepository.buscarFornecedorPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			fornecedorRepository.deletarFornecedorPorId(id);
			return true;
		}
	}
}
