package com.apiestudar.api_prodify.application.usecase.produto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class PesquisasSearchBarUseCase {

	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;

	@Autowired
	public PesquisasSearchBarUseCase(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
		this.produtoRepository = produtoRepository;
		this.usuarioRepository = usuarioRepository;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaById(valorPesquisa, idUsuario);
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario) {
		return produtoRepository.efetuarPesquisaByNome(valorPesquisa, idUsuario);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List<Produto> efetuarPesquisa(String tipoPesquisa, String valorPesquisa, long idUsuario) {	
		Helper.verificarNull(idUsuario);
		Helper.verificarNull(tipoPesquisa);
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