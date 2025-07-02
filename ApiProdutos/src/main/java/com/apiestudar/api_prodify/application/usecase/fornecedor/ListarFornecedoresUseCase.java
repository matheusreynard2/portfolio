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
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class ListarFornecedoresUseCase {

    private final FornecedorRepository fornecedorRepository;

    public ListarFornecedoresUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional
	public Page<FornecedorDTO> executar(Long idUsuario, Pageable pageable) {
		Page<Fornecedor> fornecedoresPage = fornecedorRepository.listarFornecedoresPorUsuario(idUsuario, pageable);
		return Helper.mapClassToDTOPage(fornecedoresPage, FornecedorDTO.class);
	}

	@Transactional
	public List<FornecedorDTO> executar(Long idUsuario) {
		List<Fornecedor> fornecedores = fornecedorRepository.listarFornecedoresPorUsuario(idUsuario);
		return Helper.mapClassToDTOList(fornecedores, FornecedorDTO.class);
	}

	@Transactional
	public Fornecedor executar(Long id, Long idUsuario) {
		return fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario);
	}
}
