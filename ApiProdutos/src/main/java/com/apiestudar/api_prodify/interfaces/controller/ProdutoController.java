package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.application.usecase.produto.AdicionarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.AtualizarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.CalculosSobreProdutosUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.DeletarProdutoUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.ListarProdutosUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.PesquisasSearchBarUseCase;
import com.apiestudar.api_prodify.application.usecase.produto.BuscarProdutoUseCase;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/produtos")
public class ProdutoController {

	@Autowired
	private PesquisasSearchBarUseCase pesquisasUseCase;
	@Autowired
	private AdicionarProdutoUseCase adicionarProduto;
	@Autowired
	private ListarProdutosUseCase listarProdutosPorUsuario;
	@Autowired
	private AtualizarProdutoUseCase atualizarProduto;
	@Autowired
	private DeletarProdutoUseCase deletarProduto;
	@Autowired
	private CalculosSobreProdutosUseCase consultasProduto;
	@Autowired
	private BuscarProdutoUseCase buscarProduto;

	private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);

	@ApiOperation(value = "Listagem de todos os produtos cadastrados.", notes = "Faz uma busca no banco de dados retornando uma lista com todos os produtos cadastrados.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/listarProdutos")
	public Page<ProdutoDTO> listarProdutos(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam Long idUsuario) {
		return listarProdutosPorUsuario.executar(PageRequest.of(page, size), idUsuario);
	}

	@ApiOperation(value = "Adiciona/cadastra um novo produto.", notes = "Cria um novo registro de produto no banco de dados.")
	@ApiResponse(code = 200, message = "Produto cadastrado.")
	@PostMapping("/adicionarProduto")
	public ResponseEntity<ProdutoDTO> adicionarProduto(@ModelAttribute ProdutoFormDTO produtoFormDTO) throws IOException, SQLException {
		return ResponseEntity.status(HttpStatus.CREATED).body(adicionarProduto.executar(produtoFormDTO, produtoFormDTO.getImagemFile()));
	}

	@ApiOperation(value = "Atualiza as informações de um produto.", notes = "Atualiza as informações registradas no banco de dados de um produto de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto atualizado.")
	@PutMapping("/atualizarProduto/{id}")
	public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable long id, @ModelAttribute ProdutoFormDTO produtoFormDTO) throws IOException {
		return ResponseEntity.status(HttpStatus.OK).body(atualizarProduto.executar(id, produtoFormDTO, produtoFormDTO.getImagemFile()));
	}

	@ApiOperation(value = "Deleta/exclui um produto.", notes = "Faz a exclusão de um produto do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponse(code = 200, message = "Produto excluído.")
	@DeleteMapping("/deletarProduto/{id}")
	public ResponseEntity<Boolean> deletarProduto(@PathVariable int id) {
		return ResponseEntity.status(HttpStatus.OK).body(deletarProduto.executar(id));
	}

	@ApiOperation(value = "Exibe o produto mais caro.", notes = "Exibe o valor unitário do produto mais caro entre todos os produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de preço mais caro efetuado.")
	@GetMapping("/produtoMaisCaro/{idUsuario}")
	public ResponseEntity<Object> listarProdutoMaisCaro(@PathVariable long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(consultasProduto.listarProdutoMaisCaro(idUsuario));
	}

	@ApiOperation(value = "Exibe a média de preço dos produtos.", notes = "Exibe a média de preço entre os valores unitários dos produtos registrados no banco de dados.")
	@ApiResponse(code = 200, message = "Cálculo de média de preços efetuado.")
	@GetMapping("/mediaPreco/{idUsuario}")
	public ResponseEntity<Object> obterMediaPreco(@PathVariable long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(consultasProduto.obterMediaPreco(idUsuario));
	}

	@ApiOperation(value = "Cálculo de valor de desconto.", notes = "Calcula o valor que será reduzido do preço do produto de acordo com a porcentagem de desconto passada pelo usuário.")
	@ApiResponse(code = 200, message = "Cálculo de desconto efetuado.")
	@GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
	public ResponseEntity<Object> calcularValorDesconto(@PathVariable double valorProduto,
			@PathVariable double valorDesconto) {
		return ResponseEntity.status(HttpStatus.OK).body(consultasProduto.calcularValorComDesconto(valorProduto, valorDesconto));
	}

	// Método responsável por retornar o resultado da barra de pesquisa do
	// front-end. Filtra por id ou por nome dependendo do que o usuário escolheu
	@ApiOperation(value = "Pesquisar registros por 'id' ou por 'nome'.", notes = "Faz uma busca de registros no banco de dados utilizando como filtro o id do produto ou o nome do produto.")
	@ApiResponse(code = 200, message = "Produtos encontrados.")
	@GetMapping("/efetuarPesquisa/{tipoPesquisa}/{valorPesquisa}/{idUsuario}")
	public ResponseEntity<List<ProdutoDTO>> efetuarPesquisa(@PathVariable String tipoPesquisa,
			@PathVariable String valorPesquisa, @PathVariable long idUsuario) {
		List<Produto> produtos = pesquisasUseCase.efetuarPesquisa(tipoPesquisa, valorPesquisa, idUsuario);
		List<ProdutoDTO> produtoDTOs = Helper.mapClassToDTOList(produtos, ProdutoDTO.class);
		return ResponseEntity.status(HttpStatus.OK).body(produtoDTOs);
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "Acessar a página de cadastrar produto.", notes = "Endpoint usado para validação de Token pelo front.")
	@ApiResponse(code = 200, message = "Página retornada.")
	@GetMapping("/acessarPaginaCadastro")
	public ResponseEntity acessarPaginaCadastro() {
		return new ResponseEntity(HttpStatus.OK);
	}

	@ApiOperation(value = "Busca um produto pelo ID.", notes = "Retorna os dados de um produto específico de acordo com o ID fornecido.")
	@ApiResponse(code = 200, message = "Produto encontrado.")
	@GetMapping("/buscarProduto/{id}/{idUsuario}")
	public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id, @PathVariable Long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(buscarProduto.executar(id, idUsuario));
	}
}
