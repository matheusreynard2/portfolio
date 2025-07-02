package com.apiestudar.api_prodify.application.usecase.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class DeletarProdutoUseCase {

	private final ProdutoRepository produtoRepository;
	private final ExecutorService dbPool;

	public DeletarProdutoUseCase(ProdutoRepository produtoRepository, ExecutorService dbPool ) {
		this.produtoRepository = produtoRepository;
		this.dbPool = dbPool;
	}

	@Transactional(rollbackFor = Exception.class)
	public CompletableFuture<Boolean> executar(long id) {
		return CompletableFuture.supplyAsync(() -> {
			long t0 = System.nanoTime();
			if (produtoRepository.buscarProdutoPorId(id).isEmpty()) {
				throw new RegistroNaoEncontradoException();
			}
			produtoRepository.deletarProdutoPorId(id);
			long ns = System.nanoTime() - t0;
			System.out.println("##############################");
			System.out.printf("### DELETAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
			System.out.println("##############################");
			return true;
		}, dbPool);
	}
}