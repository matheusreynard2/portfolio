package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class AtualizarProdutoUseCase {

	private final ProdutoRepository produtoRepository;

	public AtualizarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public Produto executar(long id, Produto produtoParaAtualizar, MultipartFile imagemFile) throws IOException {
		
		// Só define a imagem se ela não for nula
		if (imagemFile != null && !imagemFile.isEmpty()) {
			produtoParaAtualizar.setImagem(imagemFile.getBytes());
		}
		
		// Busca o produto pelo id e lança exceção se não encontrar
		return produtoRepository.buscarProdutoPorId(id).map(produtoExistente -> {
			// Atualiza todos os atributos do produto existente
			atualizarDadosProduto(produtoExistente, produtoParaAtualizar);
			// Salva e retorna o produto atualizado
			return produtoRepository.salvarProduto(produtoExistente);
		}).orElseThrow(RegistroNaoEncontradoException::new);
	}

	// Método auxiliar para atualizar todos os campos do produto
	private void atualizarDadosProduto(Produto produtoExistente, Produto produtoParaAtualizar) {
		produtoExistente.setNome(produtoParaAtualizar.getNome());
		produtoExistente.setDescricao(produtoParaAtualizar.getDescricao());
		produtoExistente.setFrete(produtoParaAtualizar.getFrete());
		produtoExistente.setPromocao(produtoParaAtualizar.isPromocao());
		produtoExistente.setValorTotalDesc(produtoParaAtualizar.getValorTotalDesc());
		produtoExistente.setValorTotalFrete(produtoParaAtualizar.getValorTotalFrete());
		produtoExistente.setValor(produtoParaAtualizar.getValor());
		produtoExistente.setQuantia(produtoParaAtualizar.getQuantia());
		produtoExistente.setSomaTotalValores(produtoParaAtualizar.getSomaTotalValores());
		produtoExistente.setFreteAtivo(produtoParaAtualizar.isFreteAtivo());
		produtoExistente.setValorDesconto(produtoParaAtualizar.getValorDesconto());
		produtoExistente.setImagem(produtoParaAtualizar.getImagem());
		produtoExistente.setFornecedor(produtoParaAtualizar.getFornecedor());
	}
}