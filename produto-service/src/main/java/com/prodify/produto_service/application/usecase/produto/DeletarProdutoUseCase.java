package com.prodify.produto_service.application.usecase.produto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DeletarProdutoUseCase {

	private final ProdutoRepository repo;
	private final ExecutorService dbPool;
	private final ModelMapper mapper;

	public DeletarProdutoUseCase(ProdutoRepository repo, ExecutorService dbPool, ModelMapper mapper) {
		this.repo = repo;
		this.dbPool = dbPool;
        this.mapper = mapper;
    }

	@Transactional(rollbackFor = Exception.class)
	public CompletableFuture<Void> executar(long id) {
		return CompletableFuture.supplyAsync(() -> repo.buscarProdutoPorId(id), dbPool)
			.thenCompose(opt -> opt
				.map(p -> repo.deletarProdutoPorId(id))
				.orElseGet(() -> CompletableFuture.failedFuture(new RegistroNaoEncontradoException())));
	}
}