package com.apiestudar.api_prodify.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Compra;

@Transactional
public interface CompraRepository {

    Compra salvarCompra(Compra compra);

    Compra adicionarCompra(Compra compra, Long idUsuario);

    Optional<Compra> buscarCompraPorId(Long id);

    Boolean deletarCompraPorId(Long id);

    List<Compra> findAll();

    List<Compra> listarComprasByIdUsuario(Long idUsuario);

}
