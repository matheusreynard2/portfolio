package com.apiestudar.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Método retornou NULL")
public class RetornouNuloException extends RuntimeException {

	private static final long serialVersionUID = 7196029775947471383L;
	
	String message;

	public RetornouNuloException(String message) {
		super(message);
	}
	
}