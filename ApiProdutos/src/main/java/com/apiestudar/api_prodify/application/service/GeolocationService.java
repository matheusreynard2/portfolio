package com.apiestudar.api_prodify.application.service;

import java.util.Map;

import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.domain.model.Geolocation;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface GeolocationService {

	public Geolocation obterGeolocationByIP(String ipAdress) throws GeoLocationException;
	public Map<String, Object> obterEnderecoDetalhado(double lat, double lng);
	public double[] extrairLatiLong(String loc);
	public EnderecoFornecedor obterEnderecoViaCEP(String cep) throws JsonMappingException, JsonProcessingException;
}
