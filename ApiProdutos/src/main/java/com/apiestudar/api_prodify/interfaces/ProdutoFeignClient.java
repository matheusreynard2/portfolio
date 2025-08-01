package com.apiestudar.api_prodify.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.infrastructure.config.FeignConfig;
import com.apiestudar.api_prodify.interfaces.dto.PaginatedResponseDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;

@FeignClient(name = "produto-service", configuration = FeignConfig.class)
public interface ProdutoFeignClient {

	@GetMapping("/api/produtos/listarProdutos")
	public ResponseEntity<PaginatedResponseDTO<ProdutoDTO>> listarProdutos(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam Long idUsuario);

	@PostMapping(value = "/api/produtos/adicionarProduto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<ProdutoDTO> adicionarProduto(
		@RequestPart("produtoJson") String produtoJson,
		@RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile
	) throws IOException;

	@PutMapping(value = "/api/produtos/atualizarProduto/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<ProdutoDTO> atualizarProduto(
		@PathVariable("id") Long id,
		@RequestPart("produtoJson") String produtoJson,
		@RequestPart(value = "imagemFile", required = false) MultipartFile imagemFile
	);

	@DeleteMapping("/api/produtos/deletarProduto/{id}")
	public ResponseEntity<Void> deletarProduto(@PathVariable int id);

	@GetMapping("/api/produtos/produtoMaisCaro/{idUsuario}")
	public ResponseEntity<Optional<Produto>> listarProdutoMaisCaro(@PathVariable long idUsuario);

	@GetMapping("/api/produtos/mediaPreco/{idUsuario}")
	public ResponseEntity<Object> obterMediaPreco(@PathVariable long idUsuario);

	@GetMapping("/api/produtos/calcularDesconto/{valorProduto}/{valorDesconto}")
	public ResponseEntity<Double> calcularValorDesconto(@PathVariable double valorProduto,
			@PathVariable double valorDesconto);

	@GetMapping("/api/produtos/efetuarPesquisa")
	public ResponseEntity<List<ProdutoDTO>> efetuarPesquisa(
			@RequestParam long idUsuario,
			@RequestParam(required = false) Long idProduto,
			@RequestParam(required = false) String nomeProduto,
			@RequestParam(required = false) String nomeFornecedor,
			@RequestParam(required = false) Long valorInicial);

	@SuppressWarnings("rawtypes")
	@GetMapping("/api/produtos/acessarPaginaCadastro")
	public ResponseEntity acessarPaginaCadastro();

	@GetMapping("/api/produtos/buscarProduto/{id}/{idUsuario}")
	public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id, @PathVariable Long idUsuario);
}
