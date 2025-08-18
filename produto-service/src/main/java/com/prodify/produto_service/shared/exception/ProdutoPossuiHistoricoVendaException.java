package com.prodify.produto_service.shared.exception;

public class ProdutoPossuiHistoricoVendaException extends RuntimeException {

    private static final String MESSAGE = "Produto possui histórico de venda relacionado. Exclua o histórico antes de deletar o produto.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}


