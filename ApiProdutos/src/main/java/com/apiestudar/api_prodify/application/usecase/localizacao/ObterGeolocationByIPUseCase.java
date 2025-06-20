package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.dto.GeolocationDTO;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.apiestudar.api_prodify.shared.utils.ConsumidorAPI;

@Service
public class ObterGeolocationByIPUseCase {

    public ObterGeolocationByIPUseCase() {
    
    }
    
    public GeolocationDTO executar(String ipAddress) throws GeoLocationException {
        try {
            return (GeolocationDTO) ConsumidorAPI.chamarAPI("GeolocationByIP", ipAddress, "");
        } catch (Exception e) {
            throw new GeoLocationException(ipAddress);
        }
    }
    
} 