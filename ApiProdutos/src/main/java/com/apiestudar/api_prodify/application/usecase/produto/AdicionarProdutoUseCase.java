package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionTemplate;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarProdutoUseCase {
	
	@Autowired
	private final ModelMapper modelMapper = new ModelMapper();
	private final ProdutoRepository produtoRepository;
	private final ExecutorService executorService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private TransactionTemplate transactionTemplate;

	public AdicionarProdutoUseCase(ProdutoRepository produtoRepository, ExecutorService executorService) {
		this.produtoRepository = produtoRepository;
		this.executorService = executorService;
	}

	@Transactional(rollbackFor = Exception.class)
	public CompletableFuture<ProdutoDTO> executar(ProdutoFormDTO produtoFormDTO, MultipartFile imagemFile) throws IOException, InterruptedException {

		// Marcando o tempo de início
		long startTime = System.currentTimeMillis();

		CompletableFuture<Produto> gerar = CompletableFuture.supplyAsync(() -> gerarProduto(produtoFormDTO, imagemFile), executorService);
        CompletableFuture<ProdutoDTO> produtoDTO = gerar.thenApplyAsync(this::salvarProduto, executorService);
		// Marcando o tempo de fim
		long endTime = System.currentTimeMillis();

		// Calculando tempos de execução
		long executionTimeMs = endTime - startTime;
		System.out.println("################ ADICIONAR PRODUTO #################");
		System.out.println("TEMPO DE EXECUÇÃO: " + executionTimeMs + " ms");
		System.out.println("####################################################");

		return produtoDTO;
	}

	private Produto gerarProduto(ProdutoFormDTO form, MultipartFile imagem) {
		Helper.verificarNull(form);
		Helper.verificarNull(imagem);

		try {
			ProdutoDTO dto = objectMapper.readValue(form.getProdutoJson(), ProdutoDTO.class);
			Produto produto = modelMapper.map(dto, Produto.class);
			produto.setImagem(imagem.getBytes());
			return produto;
		} catch (IOException e) {
			throw new UncheckedIOException("Falha ao gerar Produto", e);
		}
	}

	private ProdutoDTO salvarProduto(Produto produto) {
        return transactionTemplate.execute(status -> {
            Produto produtoSalvo = produtoRepository.adicionarProduto(produto);
            return modelMapper.map(produtoSalvo, ProdutoDTO.class);
        });
    }
}