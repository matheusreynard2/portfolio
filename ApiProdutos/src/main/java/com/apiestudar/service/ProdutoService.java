package com.apiestudar.service;

import java.util.List;

import com.apiestudar.model.Produto;

public interface ProdutoService {

	Produto adicionarProduto(Produto produto, String imagemBase64);

	List<Produto> listarProdutos();

	Produto atualizarProduto(long id, Produto produtoAtualizado);

	boolean deletarProduto(long id);
	
	List<Produto> listarProdutoMaisCaro();

	Double obterMediaPreco();
	
	Double calcularValorDesconto(double valorProduto, double valorDesconto);
	
	List<Produto> efetuarPesquisaById(Long valorPesquisa);
	
	List<Produto> efetuarPesquisaByNome(String valorPesquisa);
}