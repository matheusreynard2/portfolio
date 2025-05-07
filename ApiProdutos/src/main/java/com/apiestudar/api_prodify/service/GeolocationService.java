package com.apiestudar.api_prodify.service;

import java.util.Map;

import com.apiestudar.api_prodify.entity.EnderecoFornecedor;
import com.apiestudar.api_prodify.entity.Geolocation;
import com.apiestudar.api_prodify.exceptions.GeoLocationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface GeolocationService {

	public Geolocation obterGeolocationByIP(String ipAdress) throws GeoLocationException;
	public Map<String, Object> obterEnderecoDetalhado(double lat, double lng);
	public double[] extrairLatiLong(String loc);
	public EnderecoFornecedor obterEnderecoViaCEP(String cep) throws JsonMappingException, JsonProcessingException;
}
