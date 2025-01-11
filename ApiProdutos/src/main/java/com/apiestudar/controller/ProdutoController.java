package com.apiestudar.controller;

import com.apiestudar.model.Produto;
import com.apiestudar.service.ProdutoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/produtos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	@GetMapping("/listarProdutos")
	public List<Produto> listarProdutos() {
		List<Produto> produtos = produtoService.listarProdutos();
		return produtos;
	}

	@PostMapping("/adicionarProduto")
	public Produto adicionarProduto(@RequestBody Produto produto) {
		Produto produtoAdicionado = (Produto) produtoService.adicionarProduto(produto);
		return produtoAdicionado;
	}

	@PutMapping("/atualizarProduto/{id}")
	public Produto atualizarProduto(@PathVariable long id, @RequestBody Produto produtoAtualizado) {
		Produto produto = produtoService.atualizarProduto(id, produtoAtualizado);
		// Se o produto for encontrado pelo id e for atualizado
		if (produto != null)
			return produto;
		// Se o produto não for encontrado pelo id e não for atualizado
		else
			return null;
	}

	@DeleteMapping("/deletarProduto/{id}")
	public boolean deletarProduto(@PathVariable int id) {
		boolean estaDeletado = produtoService.deletarProduto(id);
		// Se encontrar o produto pelo id, o retorno é TRUE
		return estaDeletado;
	}
	
	
	@GetMapping("/produtoMaisCaro")
	public List<Produto> listarProdutoMaisCaro() {
		List<Produto> produtoMaisCaro = produtoService.listarProdutoMaisCaro();
		return produtoMaisCaro;
	}
	
	@GetMapping("/mediaPreco")
	public Double obterMediaPreco() {
		Double media = produtoService.obterMediaPreco();
		return media;
	}
			
	@GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
	public Double calcularValorDesconto(@PathVariable double valorProduto, @PathVariable double valorDesconto) {
		Double valorComDesconto = produtoService.calcularValorDesconto(valorProduto, valorDesconto);
		return valorComDesconto;
	}

}
  
