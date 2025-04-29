package com.apiestudar.exceptions;

public class ParametroInformadoNullException extends RuntimeException {

	private static final long serialVersionUID = 3512295965178723038L;

	private static final String MESSAGE = "O parâmetro informado não pode ser null.";

	public String getMessage() {
		return MESSAGE;
	}
}