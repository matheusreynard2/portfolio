package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.FornecedorJpaRepository;

@Repository
public class FornecedorRepositoryImpl implements FornecedorRepository {
	
	private final FornecedorJpaRepository fornecedorJpaRepository;
	
    public FornecedorRepositoryImpl(FornecedorJpaRepository fornecedorJpaRepository) {
        this.fornecedorJpaRepository = fornecedorJpaRepository;
    }
	
	@Override
	public Fornecedor adicionarFornecedor(Fornecedor fornecedor) {
		return fornecedorJpaRepository.save(fornecedor);	
	}
	
	@Override
	public Page<Fornecedor> listarFornecedores(Pageable pageable) {
		return fornecedorJpaRepository.findAll(pageable);
	}

}
