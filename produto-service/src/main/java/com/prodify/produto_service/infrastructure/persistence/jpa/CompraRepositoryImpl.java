package com.prodify.produto_service.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Compra;
import com.prodify.produto_service.domain.repository.CompraRepository;
import com.prodify.produto_service.infrastructure.persistence.jpa.CompraJpaRepository;

@Repository
@Transactional
public class CompraRepositoryImpl implements CompraRepository {

    private final CompraJpaRepository compraJpaRepository;

    public CompraRepositoryImpl(CompraJpaRepository compraJpaRepository) {
        this.compraJpaRepository = compraJpaRepository;
    }

    @Override
    public Compra salvarCompra(Compra compra) {
        return compraJpaRepository.save(compra);
    }

    @Override
    public Compra adicionarCompra(Compra compra, Long idUsuario) {
        compra.setIdUsuario(idUsuario);
        return compraJpaRepository.save(compra);
    }

    @Override
    public Optional<Compra> buscarCompraPorId(Long id) {
        return compraJpaRepository.findById(id);
    }

    @Override
    public Boolean deletarCompraPorId(Long id) {
        if (compraJpaRepository.existsById(id)) {
            compraJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Compra> findAll() {
        return compraJpaRepository.findAll();
    }

    @Override
    public List<Compra> listarComprasByIdUsuario(Long idUsuario) {
        return compraJpaRepository.listarComprasByIdUsuario(idUsuario);
    }

    @Override
    public Boolean existsHistoricoCompraByProdutoId(Long produtoId) {
        return compraJpaRepository.existsHistoricoCompraByProdutoId(produtoId);
    }
}
