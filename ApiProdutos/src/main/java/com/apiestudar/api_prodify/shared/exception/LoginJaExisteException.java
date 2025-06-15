package com.apiestudar.api_prodify.shared.exception;

public class LoginJaExisteException extends RuntimeException {

	private static final String MESSAGE = "Login já cadastrado no banco de dados.";

	public String getMessage() {
		return MESSAGE;
	}
}