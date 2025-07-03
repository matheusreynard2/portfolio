package com.apiestudar.api_prodify.application.usecase.produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class CalculosSobreProdutosUseCase {

	private final ProdutoRepository produtoRepository;
	private final ExecutorService cpuPool;

	public CalculosSobreProdutosUseCase(ProdutoRepository produtoRepository, @Qualifier("cpuPool") ExecutorService cpuPool) {
		this.produtoRepository = produtoRepository;
		this.cpuPool = cpuPool;
	}
	
	public Optional<Produto> listarProdutoMaisCaro(Long idUsuario) {
		long t0 = System.nanoTime();
		Optional<Produto> produto = produtoRepository.listarProdutoMaisCaro(idUsuario);
		Helper.verificarNull(idUsuario);
		long ns = System.nanoTime() - t0;
		System.out.println("##############################");
		System.out.printf("### LISTAR PRODUTO MAIS CARO %d ns ( %d ms)%n", ns, ns / 1_000_000);
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
		System.out.printf("### OBTER MEDIA PRECO %d ns ( %d ms)%n", ns, ns / 1_000_000);
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
            }, cpuPool);

        /* Aplica o desconto                                           */
        CompletableFuture<Double> futuro =
            preparar.thenApplyAsync(arr -> arr[0] - (arr[0] * arr[1]), cpuPool);

        /* log de desempenho, padrão dos últimos códigos */
        return futuro.whenComplete((r, ex) -> {
            long ns = System.nanoTime() - t0;
            System.out.printf(
                "########## DESCONTO CALCULADO ##########%n" +
                "VALOR FINAL: %.2f%n" +
                "TEMPO PROCESSAMENTO: %d ns ( %d ms)%n" +
                "########################################%n",
                r, ns, ns / 1_000_000);
        });
    }
}
