package com.prodify.produto_service.shared.exception;

public class ProdutoPossuiHistoricoCompraException extends RuntimeException {

    private static final String MESSAGE = "Produto possui histórico de compra relacionado. Exclua o histórico antes de deletar o produto.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}


