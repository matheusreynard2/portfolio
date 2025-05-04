package com.apiestudar.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.entity.EnderecoGeolocation;
import com.apiestudar.entity.Geolocation;
import com.apiestudar.service.GeolocationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class GeolocationServiceImpl implements GeolocationService {

	@Value("${ipinfo.token}")
	private String ipinfoToken;

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Geolocation obterGeolocation(String ipAddress) {
		try {
			String response = Unirest.get("https://ipinfo.io/" + ipAddress).header("Accept", "application/json")
					.queryString("token", ipinfoToken).asString().getBody();
			return objectMapper.readValue(response, Geolocation.class);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao obter geolocalização: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public  Map<String, Object> obterEnderecoDetalhado(double lat, double lng) {
	    try {        
	        // Formatação mais cuidadosa das coordenadas
	        String coordenadas = String.format(Locale.US, "%f,%f", lat, lng);
	        
	        String url = String.format(
	            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s&key=%s", 
	            coordenadas, googleMapsApiKey
	        );
	        
	        System.out.println("URL de geocodificação: " + url); // Log para depuração
	        
	        String response = Unirest.get(url)
	                .header("Accept", "application/json")
	                .asString().getBody();
	                
	        return objectMapper.readValue(response, HashMap.class);
	    } catch (Exception e) {
	        e.printStackTrace(); // Mais detalhes de erro para depuração
	        throw new RuntimeException("Erro ao obter endereço detalhado: " + e.getMessage());
	    }
	}

	@Override
	public double[] extrairLatiLong(String loc) {
		if (loc == null || loc.isEmpty()) {
			return new double[] { 0, 0 };
		}
		String[] coords = loc.split(",");
		if (coords.length != 2) {
			return new double[] { 0, 0 };
		}
		try {
			return new double[] { Double.parseDouble(coords[0]), Double.parseDouble(coords[1]) };
		} catch (NumberFormatException e) {
			return new double[] { 0, 0 };
		}
	}
}