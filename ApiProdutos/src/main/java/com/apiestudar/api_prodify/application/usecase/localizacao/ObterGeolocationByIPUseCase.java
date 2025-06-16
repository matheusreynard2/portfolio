package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.dto.GeolocationDTO;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterGeolocationByIPUseCase {

	@Value("${ipinfo.token}")
	private String ipinfoToken;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public ObterGeolocationByIPUseCase() {
    
    }
    
	public GeolocationDTO executar(String ipAddress) throws GeoLocationException {
		try {
			String response = Unirest.get("https://ipinfo.io/" + ipAddress).header("Accept", "application/json")
					.queryString("token", ipinfoToken).asString().getBody();
			return objectMapper.readValue(response, GeolocationDTO.class);
		} catch (Exception e) {
			throw new GeoLocationException(ipAddress);
		}
	}
    
}