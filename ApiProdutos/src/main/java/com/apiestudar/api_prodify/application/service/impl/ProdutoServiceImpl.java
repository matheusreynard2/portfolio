package com.apiestudar.api_prodify.application.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProdutoServiceImpl implements ProdutoService {
	
	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;

	@Autowired
	public ProdutoServiceImpl(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

	private Produto addImage(String produtoJSON, MultipartFile imagemFile) throws IOException {
		
		// Converter o JSON de volta para um objeto Produto
        Produto prod = new ObjectMapper().readValue(produtoJSON, Produto.class);
        // Converter MultipartFile para String Base 64
        String imagemStringBase64 = Helper.convertToBase64(imagemFile);
        prod.setImagem(imagemStringBase64);
        return prod;
        
	}
	
	private void verificarNull(Object parametro) {
		Optional.ofNullable(parametro)
        .orElseThrow(() -> new ParametroInformadoNullException());
	}
	
	@Transactional
	public Produto adicionarProduto(String produtoJSON, MultipartFile imagemFile) throws SQLException, IOException {
		
		verificarNull(produtoJSON);
        Produto produto = addImage(produtoJSON, imagemFile);
        // Gera o OID do Lob
        Long oid = Helper.gerarOIDfromBase64(produto.getImagem());
        // Garante permissão para acessar o Lob para o Usuário Owner do Banco de Dados
        garantirPermissaoLob(oid);
        return produtoRepository.adicionarProduto(produto);
	}

	@Transactional
	@Override
	public Page<Produto> listarProdutos(Pageable pageable) {
		return produtoRepository.listarProdutos(pageable);
	}

	@Transactional
	@Override
	public Produto atualizarProduto(long id, String produtoJSON, MultipartFile imagemFile) throws IOException {
	    // Adiciona a imagem e converte o JSON para objeto Produto
	    Produto produtoAtualizado = addImage(produtoJSON, imagemFile);
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
	public boolean deletarProduto(long id) {
		if (produtoRepository.buscarProdutoPorId(id).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {
			produtoRepository.deletarProdutoPorId(id);
			return true;
		}
	}

	@Transactional
	@Override
	public List<Produto> listarProdutoMaisCaro(long idUsuario) {
		verificarNull(idUsuario);
		return produtoRepository.listarProdutoMaisCaro(idUsuario);
	}

	@Transactional
	@Override
	public Double obterMediaPreco(long idUsuario) {
		verificarNull(idUsuario);
		Double valor = produtoRepository.obterMediaPreco(idUsuario);
		return Optional.ofNullable(valor)
			       .filter(v -> v > 0)
			       .orElse(0.0);
	}

	@Transactional
	@Override
	public Double calcularValorDesconto(double valorProduto, double valorDesconto) {
		verificarNull(valorProduto);
		verificarNull(valorDesconto);
		double valorDescontoDecimal = valorDesconto / 100;
		double valorComDesconto = valorProduto - (valorProduto * valorDescontoDecimal);
		return valorComDesconto;
	}

	@Transactional
	@Override
	public List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaById(valorPesquisa, idUsuario);
	}

	@Transactional
	@Override
	public List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaByNome(valorPesquisa, idUsuario);
	}
	
	@Transactional
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

	public void garantirPermissaoLob(Long oid) {
		produtoRepository.garantirPermissaoLob(oid);
	}
	
}