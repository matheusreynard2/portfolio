package com.apiestudar.controller.produto;

import com.apiestudar.model.Produto;
import com.apiestudar.service.produto.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/produtos")
@CrossOrigin(origins = {"http://localhost:8080", "http://www.sistemaprodify.com", "http://www.sistemaprodify.com:8080", "http://www.sistemaprodify.com:80", "http://191.252.38.22:8080", "http://191.252.38.22:80", "http://191.252.38.22"})
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	@ApiOperation(value = "Listagem de todos os produtos cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os produtos cadastrados.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/listarProdutos")
	public Page<Produto> listarProdutos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Produto> produtos = produtoService.listarProdutos(pageable);
		return produtos;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo produto.", notes = "Cria um novo registro de produto no banco de dados.")
	@ApiResponse(code = 200, message = "Produto cadastrado.")
	@PostMapping("/adicionarProduto")
	public Produto adicionarProduto(@RequestParam String produtoJSON, @RequestParam MultipartFile imagemFile) throws IOException {
		
	    // Converter o JSON de volta para um objeto Produto
        Produto produto = new ObjectMapper().readValue(produtoJSON, Produto.class);
        
        // Converter MultipartFile para String Base 64
        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
        
        produto.setImagem(imagemStringBase64);
		
		Produto produtoAdicionado = (Produto) produtoService.adicionarProduto(produto);
		
		return produtoAdicionado;
	}

	@ApiOperation(value = "Atualiza as informações de um produto.", notes = "Atualiza as informações registradas no banco de dados de um produto de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto atualizado.")
	@PutMapping("/atualizarProduto/{id}")
	public Produto atualizarProduto(@PathVariable long id, @RequestParam String produtoJSON, @RequestParam MultipartFile imagemFile) throws IOException {
		
	    // Converter o JSON de volta para um objeto Produto
        Produto produto = new ObjectMapper().readValue(produtoJSON, Produto.class);
        
        // Converter MultipartFile para String Base 64
        String imagemStringBase64 = Base64.getEncoder().encodeToString(imagemFile.getBytes());
        
        produto.setImagem(imagemStringBase64);
		
		Produto produtoAtualizado = produtoService.atualizarProduto(id, produto);
		
		return produtoAtualizado;
	}

	@ApiOperation(value = "Deleta/exclui um produto.", notes = "Faz a exclusão de um produto do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto excluído.")
	@DeleteMapping("/deletarProduto/{id}")
	public boolean deletarProduto(@PathVariable int id) {
		boolean estaDeletado = produtoService.deletarProduto(id);
		return estaDeletado;
	}

	@ApiOperation(value = "Exibe o produto mais caro.", notes = "Exibe o valor unitário do produto mais caro entre todos os produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de preço mais caro efetuado.")
	@GetMapping("/produtoMaisCaro")
	public List<Produto> listarProdutoMaisCaro() {
		List<Produto> produtoMaisCaro = produtoService.listarProdutoMaisCaro();
		return produtoMaisCaro;
	}

	@ApiOperation(value = "Exibe a média de preço dos produtos.", notes = "Exibe a média de preço entre os valores unitários dos produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de média de preços efetuado.")
	@GetMapping("/mediaPreco")
	public Double obterMediaPreco() {
		Double media = produtoService.obterMediaPreco();
		return media;
	}

	@ApiOperation(value = "Cálculo de valor de desconto.", notes = "Calcula o valor que será reduzido do preço do produto de acordo com a porcentagem de desconto passada pelo usuário.")
	@ApiResponse(code = 200, message = "Cálculo de desconto efetuado.")
	@GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
	public Double calcularValorDesconto(@PathVariable double valorProduto, @PathVariable double valorDesconto) {
		Double valorComDesconto = produtoService.calcularValorDesconto(valorProduto, valorDesconto);
		return valorComDesconto;
	}

	// Método responsável por retornar o resultado da barra de pesquisa do
	// front-end. Filtra por id ou por nome dependendo do que o usuário escoheu
	@ApiOperation(value = "Pesquisar registros por 'id' ou por 'nome'.", notes = "Faz uma busca de registros no banco de dados utilizando como filtro o id do produto ou o nome do produto.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
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
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "Acessar a página de cadastrar produto.", notes = "Endpoint usado para validação de Token pelo front.")
	@ApiResponse(code = 200, message = "Página retornada.")
	@GetMapping("/acessarPaginaCadastro")
	public ResponseEntity acessarPaginaCadastro() {
		ResponseEntity response = new ResponseEntity(HttpStatus.OK);
		return response;
	}

}
