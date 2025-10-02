package com.apiestudar.api_prodify.application.usecase.fornecedor;

import java.util.List;

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
        long t0 = System.nanoTime();
        Page<Fornecedor> fornecedoresPage = fornecedorRepository.listarFornecedoresPorUsuario(idUsuario, pageable);
        Page<FornecedorDTO> fornecedorDTOPage = Helper.mapClassToDTOPage(fornecedoresPage, FornecedorDTO.class);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### LISTAR FORNECEDORES PAGE %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return fornecedorDTOPage;
	}

	@Transactional
	public List<FornecedorDTO> executar(Long idUsuario) {
        long t0 = System.nanoTime();
        List<Fornecedor> fornecedores = fornecedorRepository.listarFornecedoresPorUsuario(idUsuario);
        List<FornecedorDTO> fornecedorDTOList = Helper.mapClassToDTOList(fornecedores, FornecedorDTO.class);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### LISTAR FORNECEDORES LIST %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return fornecedorDTOList;
	}

	@Transactional
	public Fornecedor executar(Long id, Long idUsuario) {
		return fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario);
	}
}
