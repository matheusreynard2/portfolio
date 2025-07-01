package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import com.apiestudar.api_prodify.domain.model.Fornecedor;

public interface FornecedorRepository {

	Fornecedor adicionarFornecedor(Fornecedor fornecedor, Long idUsuario);

	Page<Fornecedor> listarFornecedores(Pageable pageable);
	
	List<Fornecedor> listarFornecedores();
	
	void deletarFornecedorPorId(Long id);

	Optional<Fornecedor> buscarFornecedorPorId(Long id);

	Page<Fornecedor> listarFornecedoresPorUsuario(Long idUsuario, Pageable pageable);
	
	List<Fornecedor> listarFornecedoresPorUsuario(Long idUsuario);
	
	Fornecedor buscarFornecedorPorIdEUsuario(Long id, Long idUsuario);

	Integer contarProdutosPorFornecedor(Long idFornecedor);

	List<Fornecedor> findAll(Specification<Fornecedor> spec);

	Optional<Fornecedor> findById(Long id);
	
}