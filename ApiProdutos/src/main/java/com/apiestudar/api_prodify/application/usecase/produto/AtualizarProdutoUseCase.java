package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarProdutoUseCase {

	@Autowired
	private ModelMapper modelMapper;
	private final ProdutoRepository produtoRepository;
	private ObjectMapper objectMapper = new ObjectMapper();

	public AtualizarProdutoUseCase(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional(rollbackFor = Exception.class)
	public ProdutoDTO executar(long id, ProdutoFormDTO produtoFormDTO, MultipartFile imagemFile) throws IOException {
		Helper.verificarNull(produtoFormDTO);
		ProdutoDTO produtoDTO = objectMapper.readValue(produtoFormDTO.getProdutoJson(), ProdutoDTO.class);	
		Produto produtoParaAtualizar = modelMapper.map(produtoDTO, Produto.class);
		Helper.verificarNull(imagemFile);
		produtoParaAtualizar.setImagem(imagemFile.getBytes());
		// Busca o produto pelo id e lança exceção se não encontrar
		Produto produtoAtualizado = produtoRepository.buscarProdutoPorId(id).map(produtoExistente -> {
			// Atualiza todos os atributos do produto existente
			atualizarDadosProduto(produtoExistente, produtoParaAtualizar);
			// Salva e retorna o produto atualizado
			return produtoRepository.salvarProduto(produtoExistente);
		}).orElseThrow(RegistroNaoEncontradoException::new);
		
		// Converte o produto atualizado para DTO e retorna
		return modelMapper.map(produtoAtualizado, ProdutoDTO.class);
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