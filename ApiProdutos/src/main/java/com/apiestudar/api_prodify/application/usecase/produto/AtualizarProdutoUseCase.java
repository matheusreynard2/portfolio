
package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarProdutoUseCase {

	private final ProdutoRepository produtoRepository;

	@Autowired
	public AtualizarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}
	
	@Autowired
	private ObjectMapper objectMapper;

	@Transactional(rollbackFor = Exception.class)
	public Produto executar(long id, String produtoJSON, MultipartFile imagemFile) throws IOException {
		Produto produtoAtualizado = objectMapper.readValue(produtoJSON, Produto.class);
		produtoAtualizado.setImagem(imagemFile.getBytes());
		// Busca o produto pelo id e lança exceção se não encontrar
		return produtoRepository.buscarProdutoPorId(id).map(produtoExistente -> {
			// Atualiza todos os atributos do produto existente
			atualizarDadosProduto(produtoExistente, produtoAtualizado);

			// Salva e retorna o produto atualizado
			return produtoRepository.salvarProduto(produtoExistente);
		}).orElseThrow(RegistroNaoEncontradoException::new);
	}

	// Método auxiliar para atualizar todos os campos do produto
	private void atualizarDadosProduto(Produto produtoExistente, Produto produtoAtualizado) {
		produtoExistente.setNome(produtoAtualizado.getNome());
		produtoExistente.setDescricao(produtoAtualizado.getDescricao());
		produtoExistente.setFrete(produtoAtualizado.getFrete());
		produtoExistente.setPromocao(produtoAtualizado.isPromocao());
		produtoExistente.setValorTotalDesc(produtoAtualizado.getValorTotalDesc());
		produtoExistente.setValorTotalFrete(produtoAtualizado.getValorTotalFrete());
		produtoExistente.setValor(produtoAtualizado.getValor());
		produtoExistente.setQuantia(produtoAtualizado.getQuantia());
		produtoExistente.setSomaTotalValores(produtoAtualizado.getSomaTotalValores());
		produtoExistente.setFreteAtivo(produtoAtualizado.isFreteAtivo());
		produtoExistente.setValorDesconto(produtoAtualizado.getValorDesconto());
		produtoExistente.setImagem(produtoAtualizado.getImagem());
		produtoExistente.setFornecedor(produtoAtualizado.getFornecedor());
	}
}