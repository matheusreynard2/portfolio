
package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Geolocation;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterGeolocationByIPUseCase {

	@Value("${ipinfo.token}")
	private String ipinfoToken;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public ObterGeolocationByIPUseCase( ) {
    
    }
    
	public Geolocation executar(String ipAddress) throws GeoLocationException {
		try {
			String response = Unirest.get("https://ipinfo.io/" + ipAddress).header("Accept", "application/json")
					.queryString("token", ipinfoToken).asString().getBody();
			return objectMapper.readValue(response, Geolocation.class);
		} catch (Exception e) {
			throw new GeoLocationException(ipAddress);
		}
	}
    
}