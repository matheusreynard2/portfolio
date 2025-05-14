package com.apiestudar.api_prodify.application.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;

public interface ProdutoService {

	Produto adicionarProduto(String produto, MultipartFile imagemFile) throws SQLException, IOException;

	Page<Produto> listarProdutos(Pageable pageable);

	Produto atualizarProduto(long id, String produtoJSON, MultipartFile imagemFile) throws IOException;

	boolean deletarProduto(long id);
	
	List<Produto> listarProdutoMaisCaro(Long idUsuario);

	Double obterMediaPreco(long idUsuario);
	
	Double calcularValorComDesconto(Double valorProduto, Double valorDesconto);
	
	List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario);
	
	List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario);
	
	List<Produto> efetuarPesquisa(String tipoPesquisa, String valorPesquisa, long idUsuario);

;}