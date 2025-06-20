package com.apiestudar.api_prodify.shared.exception;

public class ParametroInformadoNullException extends RuntimeException {

	private static final String MESSAGE = "O parâmetro informado não pode ser null.";

	public String getMessage() {
		return MESSAGE;
	}
}