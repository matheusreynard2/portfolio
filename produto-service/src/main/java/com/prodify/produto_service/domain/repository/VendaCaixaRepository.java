package com.prodify.produto_service.domain.repository;

/**
 * Porta para consultar relacionamento de histórico de vendas com produtos.
 * Implementada no ApiProdutos via Feign; aqui representamos a dependência.
 */
public interface VendaCaixaRepository {
    boolean existsHistoricoByProdutoId(Long produtoId);
}


