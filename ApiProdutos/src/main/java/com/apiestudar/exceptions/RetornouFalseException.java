package com.apiestudar.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RetornouFalseException extends RuntimeException {
	
	private static final long serialVersionUID = 146562458105476346L;
	
	String message;

	public RetornouFalseException(String message) {
		super(message);
	}
	
}