package com.apiestudar.api_prodify.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.entity.Produto;
import com.apiestudar.api_prodify.exceptions.ParametroInformadoNullException;
import com.apiestudar.api_prodify.exceptions.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.service.ProdutoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("api/produtos")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "https://www.sistemaprodify.com",
		"https://www.sistemaprodify.com:8080", "https://www.sistemaprodify.com:80", "https://191.252.38.22:8080",
		"https://191.252.38.22:80", "https://191.252.38.22" }, allowedHeaders = { "*" })
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);

	@ApiOperation(value = "Listagem de todos os produtos cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os produtos cadastrados.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/listarProdutos")
	public Page<Produto> listarProdutos(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Produto> produtos = produtoService.listarProdutos(pageable);
		return produtos;
	}

	@ApiOperation(value = "Adiciona/cadastra um novo produto.", notes = "Cria um novo registro de produto no banco de dados.")
	@ApiResponse(code = 200, message = "Produto cadastrado.")
	@PostMapping("/adicionarProduto")
	public ResponseEntity<Object> adicionarProduto(@RequestParam String produtoJSON,
			@RequestParam MultipartFile imagemFile) throws IOException, SQLException {
		Produto produtoAdicionado = produtoService.adicionarProduto(produtoJSON, imagemFile);
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoAdicionado);
	}

	@ApiOperation(value = "Atualiza as informações de um produto.", notes = "Atualiza as informações registradas no banco de dados de um produto de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto atualizado.")
	@PutMapping("/atualizarProduto/{id}")
	public ResponseEntity<Object> atualizarProduto(@PathVariable long id, @RequestParam String produtoJSON,
			@RequestParam MultipartFile imagemFile) throws IOException {
		Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoJSON, imagemFile);
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoAtualizado);
	}

	@ApiOperation(value = "Deleta/exclui um produto.", notes = "Faz a exclusão de um produto do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto excluído.")
	@DeleteMapping("/deletarProduto/{id}")
	public ResponseEntity<Object> deletarProduto(@PathVariable int id) {
		produtoService.deletarProduto(id);
		return ResponseEntity.status(HttpStatus.OK).body("Deletou com sucesso");

	}

	@ApiOperation(value = "Exibe o produto mais caro.", notes = "Exibe o valor unitário do produto mais caro entre todos os produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de preço mais caro efetuado.")
	@GetMapping("/produtoMaisCaro/{idUsuario}")
	public ResponseEntity<Object> listarProdutoMaisCaro(@PathVariable long idUsuario) {
		List<Produto> produtoMaisCaro = produtoService.listarProdutoMaisCaro(idUsuario);
		return ResponseEntity.status(HttpStatus.OK).body(produtoMaisCaro);

	}

	@ApiOperation(value = "Exibe a média de preço dos produtos.", notes = "Exibe a média de preço entre os valores unitários dos produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de média de preços efetuado.")
	@GetMapping("/mediaPreco/{idUsuario}")
	public ResponseEntity<Object> obterMediaPreco(@PathVariable long idUsuario) {
		Double media = produtoService.obterMediaPreco(idUsuario);
		return ResponseEntity.status(HttpStatus.OK).body(media);

	}

	@ApiOperation(value = "Cálculo de valor de desconto.", notes = "Calcula o valor que será reduzido do preço do produto de acordo com a porcentagem de desconto passada pelo usuário.")
	@ApiResponse(code = 200, message = "Cálculo de desconto efetuado.")
	@GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
	public ResponseEntity<Object> calcularValorDesconto(@PathVariable double valorProduto,
			@PathVariable double valorDesconto) {
		Double valorComDesconto = produtoService.calcularValorDesconto(valorProduto, valorDesconto);
		return ResponseEntity.status(HttpStatus.OK).body(valorComDesconto);
	}

	// Método responsável por retornar o resultado da barra de pesquisa do
	// front-end. Filtra por id ou por nome dependendo do que o usuário escolheu
	@ApiOperation(value = "Pesquisar registros por 'id' ou por 'nome'.", notes = "Faz uma busca de registros no banco de dados utilizando como filtro o id do produto ou o nome do produto.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/efetuarPesquisa/{tipoPesquisa}/{valorPesquisa}/{idUsuario}")
	public ResponseEntity<Object> efetuarPesquisa(@PathVariable String tipoPesquisa, @PathVariable String valorPesquisa,
			@PathVariable long idUsuario) {
		List<Produto> produtos = produtoService.efetuarPesquisa(tipoPesquisa, valorPesquisa, idUsuario);
		return ResponseEntity.status(HttpStatus.OK).body(produtos);
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
