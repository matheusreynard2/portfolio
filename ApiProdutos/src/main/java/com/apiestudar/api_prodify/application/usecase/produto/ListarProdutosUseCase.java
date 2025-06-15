package com.apiestudar.api_prodify.application.usecase.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class ListarProdutosUseCase {

	private final ProdutoRepository produtoRepository;

	public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public Page<ProdutoDTO> executar(Pageable pageable, Long idUsuario) {
		Page<Produto> produtosPage = produtoRepository.listarProdutosByIdUsuario(pageable, idUsuario);
		return Helper.mapClassToDTOPage(produtosPage, ProdutoDTO.class);
	}
}