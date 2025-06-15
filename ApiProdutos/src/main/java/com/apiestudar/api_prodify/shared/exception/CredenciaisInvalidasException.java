package com.apiestudar.api_prodify.shared.exception;

public class CredenciaisInvalidasException extends RuntimeException {

	private static final String MESSAGE = "Credenciais inválidas.";

	public String getMessage() {
		return MESSAGE;
	}
} 