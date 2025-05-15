package com.apiestudar.api_prodify.application.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.application.GeolocationService;
import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.domain.model.Geolocation;
import com.apiestudar.api_prodify.interfaces.controller.LocalizacaoController;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.apiestudar.api_prodify.shared.exception.ObterCoordenadasViaCEPException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class GeolocationServiceImpl implements GeolocationService {

	// VARIÁVEIS
	@Value("${ipinfo.token}")
	private String ipinfoToken;
	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final static String API_GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
	private final static String API_VIA_CEP = "https://viacep.com.br/ws/";
	private static final Logger log = LoggerFactory.getLogger(LocalizacaoController.class);

	@Override
	public EnderecoFornecedor obterEnderecoViaCEP(String cep) throws JsonMappingException, JsonProcessingException {
		String response = Unirest.get(API_VIA_CEP + cep + "/json/").header("Accept", "application/json").asString()
				.getBody();
		return objectMapper.readValue(response, EnderecoFornecedor.class);
	}

	@Override
	public Geolocation obterGeolocationByIP(String ipAddress) throws GeoLocationException {
		try {
			String response = Unirest.get("https://ipinfo.io/" + ipAddress).header("Accept", "application/json")
					.queryString("token", ipinfoToken).asString().getBody();
			return objectMapper.readValue(response, Geolocation.class);
		} catch (Exception e) {
			throw new GeoLocationException(ipAddress);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> obterEnderecoDetalhado(double lat, double lng) {
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> obterCoordenadasPorCep(String cep) throws ObterCoordenadasViaCEPException, JsonMappingException, JsonProcessingException {
		String cepFormatado = cep.replaceAll("\\D", "");

		String url = String.format(API_GOOGLE_MAPS_URL + "address=%s&region=br&key=%s", cepFormatado,
				googleMapsApiKey);

		log.info("URL de geocodificação - obtendo Latitude e Longitude pelo CEP: {}", cepFormatado);

		String response = Unirest.get(url).header("Accept", "application/json").asString().getBody();

		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = objectMapper.readValue(response, HashMap.class);

		// Verifica se a resposta é válida
		List<Map<String, Object>> results = (List<Map<String, Object>>) resultMap.get("results");
		String status = (String) resultMap.get("status");
		if (!"OK".equals(status)) {
			resultMap.put("error", "Erro ao obter coordenadas via CEP");
			return resultMap;
		}

		Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
		Map<String, Object> location = (Map<String, Object>) geometry.get("location");
		
		Double latitude = (Double) location.get("lat");
		Double longitude = (Double) location.get("lng");

		// Retorna as coordenadas
		Map<String, Object> coordenadas = new HashMap<>();
		coordenadas.put("latitude", latitude);
		coordenadas.put("longitude", longitude);

		return coordenadas;
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