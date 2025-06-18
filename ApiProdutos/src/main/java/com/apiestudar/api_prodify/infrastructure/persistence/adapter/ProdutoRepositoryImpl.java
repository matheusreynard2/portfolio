package com.apiestudar.api_prodify.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.ProdutoJpaRepository;

@Repository
@Transactional
public class ProdutoRepositoryImpl implements ProdutoRepository {

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
    public List<Produto> listarProdutoMaisCaro(Long idUsuario) {
        return produtoJpaRepository.listarProdutoMaisCaro(idUsuario);
    }

    @Override
    public BigDecimal obterMediaPreco(Long idUsuario) {
        return produtoJpaRepository.obterMediaPreco(idUsuario);
    }

    @Override
    public List<Produto> efetuarPesquisaById(Long valorPesquisa, Long idUsuario) {
        return produtoJpaRepository.efetuarPesquisaById(valorPesquisa, idUsuario);
    }

    @Override
    public List<Produto> efetuarPesquisaByNome(String valorPesquisa, Long idUsuario) {
        return produtoJpaRepository.efetuarPesquisaByNome(valorPesquisa, idUsuario);
    }
}
