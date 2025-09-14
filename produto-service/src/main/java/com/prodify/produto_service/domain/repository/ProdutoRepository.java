package com.prodify.produto_service.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Produto;

@Transactional
public interface ProdutoRepository {

    Produto salvarProduto(Produto produto);

    Produto adicionarProduto(Produto produto);

    Optional<Produto> buscarProdutoPorId(Long id);

    Page<Produto> listarProdutosByIdUsuario(Pageable pageable, Long idUsuario);

    CompletableFuture<Void> deletarProdutoPorId(Long id);

    Optional<Produto> listarProdutoMaisCaro(Long idUsuario);

    BigDecimal obterMediaPreco(Long idUsuario);

    List<Produto> findAll(Specification<Produto> spec);

    Optional<Produto> findByIdJoinFetch(Long id);

    List<Produto> findAllJoinFetchByIds(List<Long> ids);


    List<Long> findProdutoIdsComHistoricoVenda(List<Long> ids);


    List<Long> findProdutoIdsComHistoricoCompra(List<Long> ids);


    List<Object[]> findIdENomeByIds(List<Long> ids);


    int deleteByIds(List<Long> ids);
}
