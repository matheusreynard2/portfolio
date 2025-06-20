package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.dto.LatitudeLongitudeDTO;
import com.apiestudar.api_prodify.shared.utils.ConsumidorAPI;

@Service
public class ObterCoordenadasByCEPUseCase {

    public ObterCoordenadasByCEPUseCase() {
    
    }

    public LatitudeLongitudeDTO executar(String cep) {
        return (LatitudeLongitudeDTO) ConsumidorAPI.chamarAPI("CoordenadasByCEP", cep, "");
    }

} 