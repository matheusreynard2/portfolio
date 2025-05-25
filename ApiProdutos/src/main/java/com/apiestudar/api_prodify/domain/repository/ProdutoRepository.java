package com.apiestudar.api_prodify.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apiestudar.api_prodify.domain.model.Produto;

public interface ProdutoRepository {

    Produto adicionarProduto(Produto produto);
    
    Produto salvarProduto(Produto produto);

    Optional<Produto> buscarProdutoPorId(Long id);

    Page<Produto> listarProdutos(Pageable pageable);

    void deletarProdutoPorId(Long id);

    List<Produto> listarProdutoMaisCaro(Long idUsuario);

    Double obterMediaPreco(Long idUsuario);

    List<Produto> efetuarPesquisaById(Long valorPesquisa, Long idUsuario);

    List<Produto> efetuarPesquisaByNome(String valorPesquisa, Long idUsuario);
    
}
