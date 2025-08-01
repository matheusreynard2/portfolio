package com.prodify.produto_service.shared.exception;

public class GeoLocationException extends Exception {
	
	private static final String MESSAGE = "Erro ao obter geolocalização para o IP: ";
	
	private String ipAddress;

	public GeoLocationException(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMessage() {
		return MESSAGE + ipAddress;
	}
}