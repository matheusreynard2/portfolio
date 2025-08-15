package com.prodify.produto_service.application.usecase.produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.shared.utils.Helper;

public class CalculosSobreProdutosUseCase {

	private final ProdutoRepository produtoRepository;
	private final ExecutorService dbPool;

	public CalculosSobreProdutosUseCase(ProdutoRepository produtoRepository, ExecutorService dbPool) {
		this.produtoRepository = produtoRepository;
		this.dbPool = dbPool;
	}
	
	public Optional<Produto> listarProdutoMaisCaro(Long idUsuario) {
		long t0 = System.nanoTime();
		Optional<Produto> produto = produtoRepository.listarProdutoMaisCaro(idUsuario);
		Helper.verificarNull(idUsuario);
		long ns = System.nanoTime() - t0;
		System.out.println("##############################");
		System.out.printf("### PRODUTO MAIS CARO %d ns ( %d ms)%n", ns, ns / 1_000_000);
		System.out.println("##############################");
		return produto;
	}

	public Double obterMediaPreco(long idUsuario) {
		long t0 = System.nanoTime();
		Helper.verificarNull(idUsuario);
		BigDecimal valor = produtoRepository.obterMediaPreco(idUsuario);
		Double media = Optional.ofNullable(valor)
			       .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
				   .map(BigDecimal::doubleValue)
			       .orElse(0.0);
		long ns = System.nanoTime() - t0;
		System.out.println("##############################");
		System.out.printf("### MEDIA PRECO %d ns ( %d ms)%n", ns, ns / 1_000_000);
		System.out.println("##############################");
		return media;
	}

	public CompletableFuture<Double> calcularValorComDescontoAsync(
            Double valorProduto, Double valorDesconto) {

        long t0 = System.nanoTime();                // métrica opcional

        /* Valida + normaliza (oscilação mínima de CPU) */
        CompletableFuture<double[]> preparar =
            CompletableFuture.supplyAsync(() -> {
                Helper.verificarNull(valorProduto);
                Helper.verificarNull(valorDesconto);
                return new double[] { valorProduto, valorDesconto / 100.0 }; // [0] = valor, [1] = % decimal
            }, dbPool);

        /* Aplica o desconto                                           */
        CompletableFuture<Double> futuro =
            preparar.thenApplyAsync(arr -> arr[0] - (arr[0] * arr[1]), dbPool);

        /* log de desempenho, padrão dos últimos códigos */
        return futuro.whenComplete((r, ex) -> {
            long ns = System.nanoTime() - t0;
			System.out.println("##############################");
			System.out.printf("### CALCULAR DESCONTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
			System.out.println("##############################");
        });
    }
}
