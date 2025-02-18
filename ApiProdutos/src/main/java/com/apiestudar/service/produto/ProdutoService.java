package com.apiestudar.service.produto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.model.Produto;

public interface ProdutoService {

	Produto adicionarProduto(Produto produto);

	Page<Produto> listarProdutos(Pageable pageable);

	Produto atualizarProduto(long id, Produto produtoAtualizado);

	boolean deletarProduto(long id);
	
	List<Produto> listarProdutoMaisCaro();

	Double obterMediaPreco();
	
	Double calcularValorDesconto(double valorProduto, double valorDesconto);
	
	List<Produto> efetuarPesquisaById(Long valorPesquisa);
	
	List<Produto> efetuarPesquisaByNome(String valorPesquisa);
}