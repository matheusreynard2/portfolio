package com.apiestudar.api_prodify.application.usecase.fornecedor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;

@Service
public class ListarFornecedoresUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public ListarFornecedoresUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional
	public Page<Fornecedor> executar(Long idUsuario, Pageable pageable) {
		return fornecedorRepository.listarFornecedoresPorUsuario(idUsuario, pageable);
	}

	@Transactional
	public List<Fornecedor> executar(Long idUsuario) {
		return fornecedorRepository.listarFornecedoresPorUsuario(idUsuario);
	}

	@Transactional
	public Optional<Fornecedor> executar(Long id, Long idUsuario) {
		return fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario);
	}
}
