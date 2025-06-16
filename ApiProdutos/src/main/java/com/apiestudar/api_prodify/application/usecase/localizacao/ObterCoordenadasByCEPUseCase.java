package com.apiestudar.api_prodify.application.usecase.localizacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.controller.LocalizacaoController;
import com.apiestudar.api_prodify.interfaces.dto.LatitudeLongitudeDTO;
import com.apiestudar.api_prodify.shared.exception.ObterCoordenadasViaCEPException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterCoordenadasByCEPUseCase {

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final static String API_GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static final Logger log = LoggerFactory.getLogger(LocalizacaoController.class);
	
    public ObterCoordenadasByCEPUseCase( ) {
    
    }

	@SuppressWarnings("unchecked")
	public LatitudeLongitudeDTO executar(String cep) {
		try {
			String cepFormatado = cep.replaceAll("\\D", "");

			String url = String.format(API_GOOGLE_MAPS_URL + "address=%s&region=br&key=%s", cepFormatado, googleMapsApiKey);

			log.info("URL de geocodificação - obtendo Latitude e Longitude pelo CEP: {}", cepFormatado);

			String response = Unirest.get(url).header("Accept", "application/json").asString().getBody();

			Map<String, Object> resultMap = objectMapper.readValue(response, HashMap.class);

			// Verifica se a resposta é válida
			List<Map<String, Object>> results = (List<Map<String, Object>>) resultMap.get("results");
			String status = (String) resultMap.get("status");
			if (!"OK".equals(status))
				throw new ObterCoordenadasViaCEPException();

			Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
			Map<String, Object> location = (Map<String, Object>) geometry.get("location");
	
			Double latitude = (Double) location.get("lat");
			Double longitude = (Double) location.get("lng");
	
			// Retorna as coordenadas como DTO
			return LatitudeLongitudeDTO.builder()
					.latitude(latitude)
					.longitude(longitude)
					.build();
				
		} catch (ObterCoordenadasViaCEPException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
