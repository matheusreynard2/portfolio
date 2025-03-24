package com.apiestudar.service.produto;

import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.model.Produto;

public interface ProdutoService {

	Produto adicionarProduto(Produto produto);

	Page<Produto> listarProdutos(Pageable pageable);

	Produto atualizarProduto(long id, Produto produtoAtualizado);

	boolean deletarProduto(long id);
	
	List<Produto> listarProdutoMaisCaro(long idUsuario);

	Double obterMediaPreco(long idUsuario);
	
	Double calcularValorDesconto(double valorProduto, double valorDesconto);
	
	List<Produto> efetuarPesquisaById(Long valorPesquisa, long idUsuario);
	
	List<Produto> efetuarPesquisaByNome(String valorPesquisa, long idUsuario);
	
	void garantirPermissaoLob(Long oid);
	
	Long gerarOIDfromBase64(String base64) throws SQLException

;}