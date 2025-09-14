package com.prodify.produto_service.shared.exception;

import java.util.List;

import com.prodify.produto_service.adapter.in.web.dto.ProdutoHistoricoBloqueioDTO;

// Exception que carrega a lista
public class ProdutosPossuemHistoricoRelacionadoException extends RuntimeException {
  private final List<ProdutoHistoricoBloqueioDTO> produtos;
  public ProdutosPossuemHistoricoRelacionadoException(List<ProdutoHistoricoBloqueioDTO> produtos) {
    super("Um ou mais produtos possuem histórico relacionado.");
    this.produtos = produtos;
  }
  public List<ProdutoHistoricoBloqueioDTO> getProdutos() { return produtos; }
}
