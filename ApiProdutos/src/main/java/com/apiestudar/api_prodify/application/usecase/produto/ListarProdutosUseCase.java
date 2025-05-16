
package com.apiestudar.api_prodify.application.usecase.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;

@Service
public class ListarProdutosUseCase {

	private final ProdutoRepository produtoRepository;

	@Autowired
	public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public Page<Produto> executar(Pageable pageable) {
		return produtoRepository.listarProdutos(pageable);
	}
}