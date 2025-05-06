package com.apiestudar.api_prodify.exceptions;

public class GeoLocationException extends Exception {
	
	private static final long serialVersionUID = 3923458090892426451L;
	
	private static final String MESSAGE = "Erro ao obter geolocalização para o IP: ";
	
	private String ipAddress;

	public GeoLocationException(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMessage() {
		return MESSAGE + ipAddress;
	}
}