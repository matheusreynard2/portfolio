package com.apiestudar.api_prodify.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.entity.Fornecedor;
import com.apiestudar.api_prodify.exceptions.ParametroInformadoNullException;
import com.apiestudar.api_prodify.repository.FornecedorRepository;
import com.apiestudar.api_prodify.service.FornecedorService;

@Service
public class FornecedorServiceImpl implements FornecedorService {

	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}

	public Fornecedor adicionarFornecedor(Fornecedor fornecedor) {
		verificarNull(fornecedor);
		return fornecedorRepository.save(fornecedor);	
	}

}
