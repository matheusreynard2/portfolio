package com.apiestudar.api_prodify.shared.exception;

public class FornecedorPossuiProdutoRelacionadoException extends RuntimeException {
	
	private static final String MESSAGE = "Fornecedor com produto relacionado não pode ser deletado.";

	public String getMessage() {
		return MESSAGE;
	}
}