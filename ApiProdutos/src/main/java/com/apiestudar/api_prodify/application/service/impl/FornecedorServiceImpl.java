package com.apiestudar.api_prodify.application.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.application.service.FornecedorService;
import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.shared.exception.ParametroInformadoNullException;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class FornecedorServiceImpl implements FornecedorService {

	private final FornecedorRepository fornecedorRepository;
	
	@Autowired
	public FornecedorServiceImpl(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}

	@Override
	public Fornecedor adicionarFornecedor(Fornecedor fornecedor) {
		verificarNull(fornecedor);
		return fornecedorRepository.adicionarFornecedor(fornecedor);	
	}
	
	@Override
	public Page<Fornecedor> listarFornecedores(Pageable pageable) {
		return fornecedorRepository.listarFornecedores(pageable);
	}
	
	@Override
	public boolean deletarFornecedor(long id) {
		if (fornecedorRepository.buscarFornecedorPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			fornecedorRepository.deletarFornecedorPorId(id);
			return true;
		}
	}

}
