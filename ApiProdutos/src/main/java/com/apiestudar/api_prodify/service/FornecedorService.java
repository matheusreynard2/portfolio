package com.apiestudar.api_prodify.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.entity.Fornecedor;

public interface FornecedorService {

	Fornecedor adicionarFornecedor(Fornecedor fornecedor); 
	
	Page<Fornecedor> listarFornecedores(Pageable pageable);

}
