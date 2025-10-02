package com.prodify.produto_service.application.usecase.produto;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.shared.utils.Helper;

@Service
public class CalculosSobreProdutosUseCase {

	private final ProdutoRepository produtoRepository;

    public CalculosSobreProdutosUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
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

    public Double calcularValorComDesconto(Double valorProduto, Double valorDesconto) {
        long t0 = System.nanoTime();
        Helper.verificarNull(valorProduto);
        Helper.verificarNull(valorDesconto);
        double porcentagem = valorDesconto / 100.0;
        double resultado = valorProduto - (valorProduto * porcentagem);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### CALCULAR DESCONTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return resultado;
    }
}
