package com.apiestudar.service;

import com.apiestudar.model.Produto;
import com.apiestudar.repository.ProdutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Override
	public Produto adicionarProduto(Produto produto) {
		return produtoRepository.save(produto);
	}

	@Override
	public List<Produto> listarProdutos() {
		return produtoRepository.findAll();
	}

	@Override
	public Optional<Produto> buscarProduto(long id) {
		return produtoRepository.findById(id);
	}

	@Override
	public Produto atualizarProduto(long id, Produto produtoAtualizado) {
		// Pega o produto pelo id
		Optional<Produto> produto = produtoRepository.findById(id);
		// Se o produto não existir, ou seja, se ele for nulo, retorna nulo
		if (produto.isEmpty() == true)
			return null;
		// Se o produto existir, ou seja, se não for nulo, seta os novos atributos,
		// salva e retorna pro controller
		else {
			produto.get().setNome(produtoAtualizado.getNome());
			produto.get().setDescricao(produtoAtualizado.getDescricao());
			produto.get().setValor(produtoAtualizado.getValor());
			produto.get().setQuantia(produtoAtualizado.getQuantia());
			produtoRepository.save(produto.get());
			return produto.get();
		}
	}

	@Override
	public void deletarProduto(long id) {
		produtoRepository.deleteById(id);
	}

}