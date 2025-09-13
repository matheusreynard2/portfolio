package com.prodify.produto_service.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.domain.model.Compra;

@Transactional
public interface CompraRepository {

    Compra salvarCompra(Compra compra);

    Compra adicionarCompra(Compra compra, Long idUsuario);

    Optional<Compra> buscarCompraPorId(Long id);

    Boolean deletarCompraPorId(Long id);

    List<Compra> findAll();

    List<Compra> listarComprasByIdUsuario(Long idUsuario);

    Boolean existsHistoricoCompraByProdutoId(Long produtoId);

}
