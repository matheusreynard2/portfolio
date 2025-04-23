package com.apiestudar.service.produto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.model.Produto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ProdutoService {

	Produto adicionarProduto(String produto, MultipartFile imagemFile) throws SQLException, IOException;

	Page<Produto> listarProdutos(Pageable pageable);
	
	List<Produto> listarProdutosReact();

	Produto atualizarProduto(long id, String produtoJSON, MultipartFile imagemFile) throws IOException;

	boolean deletarProduto(long id);
	
	List<Produto> listarProdutoMaisCaro(long idUsuario);

	Double obterMediaPreco(long idUsuario);
	
	Double calcularValorDesconto(double valorProduto, double valorDesconto);
	
	List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario);
	
	List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario);

;}