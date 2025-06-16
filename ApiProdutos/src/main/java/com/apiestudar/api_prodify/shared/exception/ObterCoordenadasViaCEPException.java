package com.apiestudar.api_prodify.shared.exception;

public class ObterCoordenadasViaCEPException extends Exception {
	
	private static final long serialVersionUID = 3923458090892426451L;
	
	private static final String MESSAGE = "Erro ao obter coordenadas de latitude e longitude via CEP: ";

	public String getMessage() {
		return MESSAGE;
	}
}