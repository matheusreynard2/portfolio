package com.prodify.produto_service.shared.exception;

public class CredenciaisInvalidasException extends RuntimeException {

	private static final String MESSAGE = "Credenciais inválidas.";

	public String getMessage() {
		return MESSAGE;
	}
} 