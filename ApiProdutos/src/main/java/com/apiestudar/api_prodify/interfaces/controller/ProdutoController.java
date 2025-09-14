package com.apiestudar.api_prodify.interfaces.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.interfaces.ProdutoFeignClient;
import com.apiestudar.api_prodify.interfaces.dto.PaginatedResponseDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

	private final ProdutoFeignClient produtoFeignClient;
	private final ObjectMapper objectMapper;

	@Operation(summary = "Listagem de todos os produtos cadastrados.", description = "Faz uma busca no banco de dados retornando uma lista com todos os produtos cadastrados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produtos encontrados.")
	})
	@GetMapping("/listarProdutos")
	public ResponseEntity<PaginatedResponseDTO<ProdutoDTO>> listarProdutos(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam Long idUsuario) {

		return produtoFeignClient.listarProdutos(page, size, idUsuario);
	}

	@Operation(summary = "Adiciona/cadastra um novo produto.", description = "Cria um novo registro de produto no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Produto cadastrado.")
	})
	@PostMapping(value = "/adicionarProduto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProdutoDTO> adicionarProduto(
			@RequestPart("produtoJson") String produtoJson,
			@RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile) throws IOException {
		// Encaminhar diretamente para o produto-service via Feign Client
		return produtoFeignClient.adicionarProduto(produtoJson, imagemFile);
	}

	@Operation(summary = "Atualiza as informações de um produto.", description = "Atualiza as informações registradas no banco de dados de um produto de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produto atualizado.")
	})
	@PutMapping(value = "/atualizarProduto/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProdutoDTO> atualizarProduto(
		@PathVariable("id") Long id,
		@RequestPart("produtoJson") String produtoJson,
		@RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile
	) {
		return produtoFeignClient.atualizarProduto(id, produtoJson, imagemFile);
	}

	@Operation(summary = "Deleta/exclui um produto.", description = "Faz a exclusão de um produto do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produto excluído.")
	})
	@DeleteMapping("/deletarProduto/{id}")
	public ResponseEntity<Void> deletarProduto(@PathVariable int id) {
		return produtoFeignClient.deletarProduto(id);
	}

	@Operation(summary = "Exclui múltiplos produtos.", description = "Exclui em lote produtos pelos seus IDs, validando histórico de COMPRA/VENDA antes.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Produtos excluídos."),
                    @ApiResponse(responseCode = "409", description = "Algum produto possui histórico relacionado.") })
    @DeleteMapping("/deletarMultiProdutos")
    public ResponseEntity<Void> deletarMultiProdutos(@RequestParam("ids") List<Integer> ids) {
        return produtoFeignClient.deletarMultiProdutos(ids);
    }

	@Operation(summary = "Exibe o produto mais caro.", description = "Exibe o valor unitário do produto mais caro entre todos os produtos registrados no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de preço mais caro efetuado.")
	})
	@GetMapping("/produtoMaisCaro/{idUsuario}")
	public ResponseEntity<Optional<Produto>> listarProdutoMaisCaro(@PathVariable long idUsuario) {
		return produtoFeignClient.listarProdutoMaisCaro(idUsuario);
	}

	@Operation(summary = "Exibe a média de preço dos produtos.", description = "Exibe a média de preço entre os valores unitários dos produtos registrados no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de média de preços efetuado.")
	})
	@GetMapping("/mediaPreco/{idUsuario}")
	public ResponseEntity<Object> obterMediaPreco(@PathVariable long idUsuario) {
		return produtoFeignClient.obterMediaPreco(idUsuario);
	}

	@Operation(summary = "Cálculo de valor de desconto.", description = "Calcula o valor que será reduzido do preço do produto de acordo com a porcentagem de desconto passada pelo usuário.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de desconto efetuado.")
	})
	@GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
	public ResponseEntity<Double> calcularValorDesconto(@PathVariable double valorProduto,
			@PathVariable double valorDesconto) {
		return produtoFeignClient.calcularValorDesconto(valorProduto, valorDesconto);
	}

	// Método responsável por retornar o resultado da barra de pesquisa do
	// front-end. Filtra por id ou por nome dependendo do que o usuário escolheu
	@Operation(summary = "Pesquisar registros por 'id' ou por 'nome'.", description = "Faz uma busca de registros no banco de dados utilizando como filtro o id do produto ou o nome do produto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produtos encontrados.")
	})
	@GetMapping("/efetuarPesquisa")
	public ResponseEntity<List<ProdutoDTO>> efetuarPesquisa(
			@RequestParam long idUsuario,
			@RequestParam(required = false) Long idProduto,
			@RequestParam(required = false) String nomeProduto,
			@RequestParam(required = false) String nomeFornecedor,
			@RequestParam(required = false) Long valorInicial) {
		return produtoFeignClient.efetuarPesquisa(idUsuario, idProduto, nomeProduto, nomeFornecedor, valorInicial);
	}

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Acessar a página de cadastrar produto.", description = "Endpoint usado para validação de Token pelo front.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Página retornada.")
	})
	@GetMapping("/acessarPaginaCadastro")
	public ResponseEntity acessarPaginaCadastro() {
		return new ResponseEntity(HttpStatus.OK);
	}

	@Operation(summary = "Busca um produto pelo ID.", description = "Retorna os dados de um produto específico de acordo com o ID fornecido.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produto encontrado.")
	})
	@GetMapping("/buscarProduto/{id}/{idUsuario}")
	public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id, @PathVariable Long idUsuario) {
		return produtoFeignClient.buscarProduto(id, idUsuario);
	}
}
