package com.apiestudar.controller;

import com.apiestudar.model.Produto;
import com.apiestudar.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
	public Produto adicionarProduto(@RequestParam String produtoJSON, @RequestParam MultipartFile imagemFile) throws IOException {
		
	    // Converter o JSON de volta para um objeto Produto
        Produto produto = new ObjectMapper().readValue(produtoJSON, Produto.class);
        
        // Converter MultipartFile para String Base 64
        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
        
        System.out.println(imagemStringBase64);
        
        produto.setImagem(imagemStringBase64);
		
		Produto produtoAdicionado = (Produto) produtoService.adicionarProduto(produto, imagemStringBase64);
		
		return produtoAdicionado;
	}

	@PutMapping("/atualizarProduto/{id}")
	public Produto atualizarProduto(@PathVariable long id, @RequestBody Produto produtoAtualizado) {
		Produto produto = produtoService.atualizarProduto(id, produtoAtualizado);
		if (produto != null)
			return produto;
		else
			return null;
	}

	@DeleteMapping("/deletarProduto/{id}")
	public boolean deletarProduto(@PathVariable int id) {
		boolean estaDeletado = produtoService.deletarProduto(id);
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

	// Método responsável por retornar o resultado da barra de pesquisa do
	// front-end. Filtra por id ou por nome dependendo do que o usuário escoheu
	@GetMapping("/efetuarPesquisa/{tipoPesquisa}/{valorPesquisa}")
	public List<Produto> efetuarPesquisa(@PathVariable String tipoPesquisa, @PathVariable String valorPesquisa) {
		
		List<Produto> produtos = new ArrayList<Produto>();
		
		if (tipoPesquisa.equals("id")) {
			long valorPesquisaLong = Long.parseLong(valorPesquisa);
			produtos = produtoService.efetuarPesquisaById(valorPesquisaLong);
			
		} else if (tipoPesquisa.equals("nome")) {
			produtos = produtoService.efetuarPesquisaByNome(valorPesquisa);
		}
		
		return produtos;
	}

}
