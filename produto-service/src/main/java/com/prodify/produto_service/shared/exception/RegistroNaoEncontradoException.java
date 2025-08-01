package com.prodify.produto_service.shared.exception;

public class RegistroNaoEncontradoException extends RuntimeException {
	
	private static final String MESSAGE = "Registro não encontrado no banco de dados.";

	public String getMessage() {
		return MESSAGE;
	}
}