package com.prodify.produto_service.application.usecase.produto;

import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.domain.repository.VendaCaixaRepository;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;
import com.prodify.produto_service.shared.exception.ProdutoPossuiHistoricoVendaException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DeletarProdutoUseCase {

	private final ProdutoRepository repo;
	private final VendaCaixaRepository vendaRepo;
	private final ExecutorService dbPool;

	public DeletarProdutoUseCase(ProdutoRepository repo, VendaCaixaRepository vendaRepo, ExecutorService dbPool) {
		this.repo = repo;
		this.vendaRepo = vendaRepo;
		this.dbPool = dbPool;
    }

	@Transactional(rollbackFor = Exception.class)
	public CompletableFuture<Void> executar(long id) {
        long t0 = System.nanoTime();
        return CompletableFuture.supplyAsync(() -> repo.buscarProdutoPorId(id), dbPool)
            .thenCompose(opt -> opt
                .map(p -> {
                    boolean relacionado = vendaRepo.existsHistoricoByProdutoId(id);
                    if (relacionado) {
                        throw new ProdutoPossuiHistoricoVendaException();
                    }
                    return repo.deletarProdutoPorId(id);
                })
                .orElseGet(() -> CompletableFuture.failedFuture(new RegistroNaoEncontradoException())))
            .whenComplete((r, ex) -> {
				long ns = System.nanoTime() - t0;
				System.out.println("##############################");
				System.out.printf("### DELETAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
				System.out.println("##############################");
            });
	}
}