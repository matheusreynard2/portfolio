package com.apiestudar.api_prodify.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.domain.model.Fornecedor;

public interface FornecedorRepository {

	Fornecedor adicionarFornecedor(Fornecedor fornecedor);

	Page<Fornecedor> listarFornecedores(Pageable pageable);

}