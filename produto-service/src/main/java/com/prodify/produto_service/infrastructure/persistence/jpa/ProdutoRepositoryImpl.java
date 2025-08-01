package com.prodify.produto_service.infrastructure.persistence.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.infrastructure.persistence.jpa.ProdutoJpaRepository;

@Repository
@Transactional
public class ProdutoRepositoryImpl implements ProdutoRepository {

    @PersistenceContext
    private EntityManager em;

    private final ProdutoJpaRepository produtoJpaRepository;
    private final ModelMapper mapper;
    private final ExecutorService dbPool;

    public ProdutoRepositoryImpl(ProdutoJpaRepository produtoJpaRepository, ModelMapper mapper, ExecutorService dbPool) {
        this.produtoJpaRepository = produtoJpaRepository;
        this.mapper = mapper;
        this.dbPool = dbPool;
    }

    @Override
    public Produto adicionarProduto(Produto produto) {
        return produtoJpaRepository.save(produto);
    }
    
    @Override
    public Produto salvarProduto(Produto produto) {
        return produtoJpaRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarProdutoPorId(Long id) {
        return produtoJpaRepository.findById(id);
    }

    @Override
    public Page<Produto> listarProdutosByIdUsuario(Pageable pageable, Long idUsuario) {
        return produtoJpaRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    public CompletableFuture<Void> deletarProdutoPorId(Long id) {
        return CompletableFuture.runAsync(() -> produtoJpaRepository.deleteById(id), dbPool);
    }

    @Override
    public Optional<Produto> listarProdutoMaisCaro(Long idUsuario) {
        return produtoJpaRepository.listarProdutoMaisCaro(idUsuario);
    }

    @Override
    public BigDecimal obterMediaPreco(Long idUsuario) {
        return produtoJpaRepository.obterMediaPreco(idUsuario);
    }

    @Override
    public List<Produto>findAll(Specification<Produto> spec) {
         return produtoJpaRepository.findAll(spec);
    }

    @Override
    public Optional<Produto> findByIdJoinFetch(Long id) {
        return produtoJpaRepository.findByIdJoinFetch(id);
    }

    @Override
    public List<Produto> findAllJoinFetchByIds(List<Long> ids) {
        return produtoJpaRepository.findAllJoinFetchByIds(ids);
    }
}
