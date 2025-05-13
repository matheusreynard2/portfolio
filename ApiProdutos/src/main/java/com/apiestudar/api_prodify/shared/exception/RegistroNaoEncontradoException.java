package com.apiestudar.api_prodify.shared.exception;

public class RegistroNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 3009509166142633795L;
	
	private static final String MESSAGE = "Registro não encontrado no banco de dados.";

	public String getMessage() {
		return MESSAGE;
	}
}