package com.apiestudar.api_prodify.application.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.service.ProdutoService;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.exception.ParametroInformadoNullException;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProdutoServiceImpl implements ProdutoService {
	
	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public ProdutoServiceImpl(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Produto adicionarProduto(String produtoJSON, MultipartFile imagemFile) throws SQLException, IOException {
		
		verificarNull(produtoJSON);
		Produto prod = objectMapper.readValue(produtoJSON, Produto.class);
		prod.setImagem(imagemFile.getBytes());
        return produtoRepository.adicionarProduto(prod);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Page<Produto> listarProdutos(Pageable pageable) {
		return produtoRepository.listarProdutos(pageable);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Produto atualizarProduto(long id, String produtoJSON, MultipartFile imagemFile) throws IOException {
		Produto produtoAtualizado = objectMapper.readValue(produtoJSON, Produto.class);
		produtoAtualizado.setImagem(imagemFile.getBytes());
	    // Busca o produto pelo id e lança exceção se não encontrar
	    return produtoRepository.buscarProdutoPorId(id)
	        .map(produtoExistente -> {
	            // Atualiza todos os atributos do produto existente
	            atualizarDadosProduto(produtoExistente, produtoAtualizado);
	            
	            // Salva e retorna o produto atualizado
	            return produtoRepository.salvarProduto(produtoExistente);
	        })
	        .orElseThrow(RegistroNaoEncontradoException::new);
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
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deletarProduto(long id) {
		if (produtoRepository.buscarProdutoPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			produtoRepository.deletarProdutoPorId(id);
			return true;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<Produto> listarProdutoMaisCaro(Long idUsuario) {
		verificarNull(idUsuario);
		return produtoRepository.listarProdutoMaisCaro(idUsuario);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Double obterMediaPreco(long idUsuario) {
		verificarNull(idUsuario);
		Double valor = produtoRepository.obterMediaPreco(idUsuario);
		return Optional.ofNullable(valor)
			       .filter(v -> v > 0)
			       .orElse(0.0);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Double calcularValorComDesconto(Double valorProduto, Double valorDesconto) {
		verificarNull(valorProduto);
		verificarNull(valorDesconto);
		double valorDescontoDecimal = valorDesconto / 100;
		double valorComDesconto = valorProduto - (valorProduto * valorDescontoDecimal);
		return valorComDesconto;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaById(valorPesquisa, idUsuario);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaByNome(valorPesquisa, idUsuario);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List<Produto> efetuarPesquisa(String tipoPesquisa, String valorPesquisa, long idUsuario) {	
		verificarNull(idUsuario);
		verificarNull(tipoPesquisa);
		if (usuarioRepository.buscarUsuarioPorId(idUsuario).isEmpty())
			throw new RegistroNaoEncontradoException();
		else {
			List<Produto> produtos = new ArrayList<Produto>();
			if (tipoPesquisa.equals("id")) {
				long valorPesquisaLong = Long.parseLong(valorPesquisa);
				produtos = efetuarPesquisaById(valorPesquisaLong, idUsuario);
			} else if (tipoPesquisa.equals("nome"))
				produtos = efetuarPesquisaByNome(valorPesquisa, idUsuario);
			return produtos;
		}
	}
	
}