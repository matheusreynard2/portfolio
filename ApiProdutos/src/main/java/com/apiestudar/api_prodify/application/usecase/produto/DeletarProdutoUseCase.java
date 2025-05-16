
package com.apiestudar.api_prodify.application.usecase.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarProdutoUseCase {

	private final ProdutoRepository produtoRepository;

	@Autowired
	public DeletarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean executar(long id) {
		if (produtoRepository.buscarProdutoPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			produtoRepository.deletarProdutoPorId(id);
			return true;
		}
	}
}