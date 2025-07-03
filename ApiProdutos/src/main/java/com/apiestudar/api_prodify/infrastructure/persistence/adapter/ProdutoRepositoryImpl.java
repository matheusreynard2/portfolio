package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.ProdutoJpaRepository;

@Repository
@Transactional
public class ProdutoRepositoryImpl implements ProdutoRepository {

    @PersistenceContext
    private EntityManager em;

    private final ProdutoJpaRepository produtoJpaRepository;

    public ProdutoRepositoryImpl(ProdutoJpaRepository produtoJpaRepository) {
        this.produtoJpaRepository = produtoJpaRepository;
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
    public void deletarProdutoPorId(Long id) {
        produtoJpaRepository.deleteById(id);
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
