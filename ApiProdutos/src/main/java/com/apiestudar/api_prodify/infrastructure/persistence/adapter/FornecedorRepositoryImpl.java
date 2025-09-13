package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.FornecedorJpaRepository;

@Repository
@Transactional
public class FornecedorRepositoryImpl implements FornecedorRepository {
	
	private final FornecedorJpaRepository fornecedorJpaRepository;
	
    public FornecedorRepositoryImpl(FornecedorJpaRepository fornecedorJpaRepository) {
        this.fornecedorJpaRepository = fornecedorJpaRepository;
    }
	
	@Override
	public Fornecedor adicionarFornecedor(Fornecedor fornecedor, Long idUsuario) {
		fornecedor.setIdUsuario(idUsuario);
		return fornecedorJpaRepository.save(fornecedor);	
	}
	
	@Override
	public Page<Fornecedor> listarFornecedores(Pageable pageable) {
		return fornecedorJpaRepository.findAll(pageable);
	}
	
	@Override
	public void deletarFornecedorPorId(Long id) {
		fornecedorJpaRepository.deleteById(id);
	}
	
	@Override
	public Optional<Fornecedor> buscarFornecedorPorId(Long id) {
		return fornecedorJpaRepository.findById(id);
	}

	@Override																																																																														
	public List<Fornecedor> listarFornecedores() {
		return fornecedorJpaRepository.findAll();
	}

	@Override
	public List<Fornecedor> findAll(Specification<Fornecedor> spec) {
		return fornecedorJpaRepository.findAll(spec);
	}										

	@Override
	public Page<Fornecedor> listarFornecedoresPorUsuario(Long idUsuario, Pageable pageable) {
		return fornecedorJpaRepository.findByIdUsuario(idUsuario, pageable);
	}

	@Override
	public List<Fornecedor> listarFornecedoresPorUsuario(Long idUsuario) {
		return fornecedorJpaRepository.findByIdUsuario(idUsuario);
	}

	@Override
	public Fornecedor buscarFornecedorPorIdEUsuario(Long id, Long idUsuario) {
		return fornecedorJpaRepository.findByIdAndIdUsuario(id, idUsuario);
	}

	@Override
	public Integer contarProdutosPorFornecedor(Long idFornecedor) {
		return fornecedorJpaRepository.countProdutosByFornecedorId(idFornecedor);
	}

	@Override
	public Optional<Fornecedor> findById(Long id) {
		return fornecedorJpaRepository.findById(id);
	}

}
