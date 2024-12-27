package com.apiestudar.service;

import java.util.List;
import java.util.Optional;

import com.apiestudar.model.Produto;

public interface ProdutoService {

	Produto adicionarProduto(Produto produto);

	List<Produto> listarProdutos();

	Optional<Produto> buscarProduto(long id);

	Produto atualizarProduto(long id, Produto produtoAtualizado);

	boolean deletarProduto(long id);

}