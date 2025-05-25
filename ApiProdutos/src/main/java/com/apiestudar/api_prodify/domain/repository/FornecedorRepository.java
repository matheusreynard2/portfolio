package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.domain.model.Fornecedor;

public interface FornecedorRepository {

	Fornecedor adicionarFornecedor(Fornecedor fornecedor);

	Page<Fornecedor> listarFornecedores(Pageable pageable);
	
	List<Fornecedor> listarFornecedores();
	
	void deletarFornecedorPorId(Long id);

	Optional<Fornecedor> buscarFornecedorPorId(Long id);
}