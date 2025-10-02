package com.prodify.produto_service.adapter.in.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.prodify.produto_service.application.usecase.produto.AdicionarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.AtualizarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.CalculosSobreProdutosUseCase;
import com.prodify.produto_service.application.usecase.produto.DeletarMultiProdutosUseCase;
import com.prodify.produto_service.application.usecase.produto.DeletarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.ListarProdutosUseCase;
import com.prodify.produto_service.application.usecase.produto.BuscarProdutoUseCase;
import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.adapter.in.web.dto.PaginatedResponseDTO;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

	private final AdicionarProdutoUseCase adicionarProduto;
	private final ListarProdutosUseCase listarProdutosUseCase;
	private final AtualizarProdutoUseCase atualizarProduto;
	private final DeletarProdutoUseCase deletarProduto;
	private final CalculosSobreProdutosUseCase consultasProduto;
	private final BuscarProdutoUseCase buscarProduto;
	private final DeletarMultiProdutosUseCase deletarMultiProdutos;

	@Operation(summary = "Listagem de todos os produtos cadastrados.", description = "Faz uma busca no banco de dados retornando uma lista com todos os produtos cadastrados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produtos encontrados.")
	})
	@GetMapping("/listarProdutos")
    public ResponseEntity<PaginatedResponseDTO<ProdutoDTO>> listarProdutos(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam Long idUsuario) {
	
		Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listarProdutosUseCase.executar(pageable, idUsuario));
	}
	
	@Operation(summary = "Adiciona/cadastra um novo produto.", description = "Cria um novo registro de produto no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Produto cadastrado.")
	})
    @PostMapping(value = "/adicionarProduto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoDTO> adicionarProduto(
        @RequestPart("produtoJson") String produtoJson,
        @RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(adicionarProduto.executar(produtoJson, imagemFile));
    }

	@Operation(summary = "Atualiza as informações de um produto.", description = "Atualiza as informações registradas no banco de dados de um produto de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produto atualizado.")
	})
    @PutMapping(value = "/atualizarProduto/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoDTO> atualizarProduto (@PathVariable("id") long id,
    @RequestPart("produtoJson") String produtoJson,
    @RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(atualizarProduto.executar(id, produtoJson, imagemFile));
    }

	@Operation(summary = "Deleta/exclui um produto.", description = "Faz a exclusão de um produto do banco de dados de acordo com o número de id passado como parâmetro.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Produto excluído.")
	})
	@DeleteMapping("/deletarProduto/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(deletarProduto.executar(id));
	}

	@Operation(summary = "Exclui múltiplos produtos.", description = "Exclui em lote produtos pelos seus IDs, validando histórico de COMPRA/VENDA antes.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Produtos excluídos."),
                    @ApiResponse(responseCode = "409", description = "Algum produto possui histórico relacionado.") })
    @DeleteMapping("/deletarMultiProdutos")
    public ResponseEntity<Boolean> deletarMultiProdutos(@RequestParam("ids") List<Integer> ids) {
        return ResponseEntity.status(HttpStatus.OK).body(deletarMultiProdutos.executar(ids));
    }

	@Operation(summary = "Exibe o produto mais caro.", description = "Exibe o valor unitário do produto mais caro entre todos os produtos registrados no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de preço mais caro efetuado.")
	})
	@GetMapping("/produtoMaisCaro/{idUsuario}")
	public ResponseEntity<Optional<Produto>> listarProdutoMaisCaro(@PathVariable long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(consultasProduto.listarProdutoMaisCaro(idUsuario));
	}

	@Operation(summary = "Exibe a média de preço dos produtos.", description = "Exibe a média de preço entre os valores unitários dos produtos registrados no banco de dados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de média de preços efetuado.")
	})
	@GetMapping("/mediaPreco/{idUsuario}")
	public ResponseEntity<Object> obterMediaPreco(@PathVariable long idUsuario) {
		return ResponseEntity.status(HttpStatus.OK).body(consultasProduto.obterMediaPreco(idUsuario));
	}

	@Operation(summary = "Cálculo de valor de desconto.", description = "Calcula o valor que será reduzido do preço do produto de acordo com a porcentagem de desconto passada pelo usuário.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cálculo de desconto efetuado.")
	})
    @GetMapping("/calcularDesconto/{valorProduto}/{valorDesconto}")
    public ResponseEntity<Double> calcularValorDesconto(@PathVariable double valorProduto,
            @PathVariable double valorDesconto) {
        return ResponseEntity.ok(consultasProduto.calcularValorComDesconto(valorProduto, valorDesconto));
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
		return ResponseEntity.status(HttpStatus.OK).body(buscarProduto.executar(id, idUsuario));
	}
}
