package com.apiestudar.api_prodify.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.domain.model.Fornecedor;

public interface FornecedorService {

	Fornecedor adicionarFornecedor(Fornecedor fornecedor); 
	
	Page<Fornecedor> listarFornecedores(Pageable pageable);

	boolean deletarFornecedor(long id);
}
