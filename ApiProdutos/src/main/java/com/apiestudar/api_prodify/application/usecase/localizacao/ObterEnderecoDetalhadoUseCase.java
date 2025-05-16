package com.apiestudar.api_prodify.application.usecase.localizacao;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.controller.LocalizacaoController;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterEnderecoDetalhadoUseCase {

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;
	private static final Logger log = LoggerFactory.getLogger(LocalizacaoController.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final static String API_GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";

    @Autowired
    public ObterEnderecoDetalhadoUseCase( ) {
    
    }
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> executar(double lat, double lng) {
		try {
			// Formatação mais cuidadosa das coordenadas
			String coordenadas = String.format(Locale.US, "%f,%f", lat, lng);

			String url = String.format(API_GOOGLE_MAPS_URL + "latlng=%s&key=%s", coordenadas, googleMapsApiKey);

			log.info("URL de geocodificação - obter endereco especifico: {}", url);

			String response = Unirest.get(url).header("Accept", "application/json").asString().getBody();

			return objectMapper.readValue(response, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace(); // Mais detalhes de erro para depuração
			throw new RuntimeException("Erro ao obter endereço detalhado: " + e.getMessage());
		}
	}
}
