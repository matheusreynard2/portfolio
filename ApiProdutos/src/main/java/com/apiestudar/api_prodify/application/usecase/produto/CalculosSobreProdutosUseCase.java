package com.apiestudar.api_prodify.application.usecase.produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class CalculosSobreProdutosUseCase {

	private final ProdutoRepository produtoRepository;

	public CalculosSobreProdutosUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List<Produto> listarProdutoMaisCaro(Long idUsuario) {
		Helper.verificarNull(idUsuario);
		return produtoRepository.listarProdutoMaisCaro(idUsuario);
	}

	@Transactional(rollbackFor = Exception.class)
	public Double obterMediaPreco(long idUsuario) {
		Helper.verificarNull(idUsuario);
		BigDecimal valor = produtoRepository.obterMediaPreco(idUsuario);
		return Optional.ofNullable(valor)
			       .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
				   .map(BigDecimal::doubleValue)
			       .orElse(0.0);
	}

	@Transactional(rollbackFor = Exception.class)
	public Double calcularValorComDesconto(Double valorProduto, Double valorDesconto) {
		Helper.verificarNull(valorProduto);
		Helper.verificarNull(valorDesconto);
		double valorDescontoDecimal = valorDesconto / 100;
		double valorComDesconto = valorProduto - (valorProduto * valorDescontoDecimal);
		return valorComDesconto;
	}
}
