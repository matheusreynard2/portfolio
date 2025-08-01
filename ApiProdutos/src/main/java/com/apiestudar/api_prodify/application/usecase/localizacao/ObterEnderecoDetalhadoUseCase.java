package com.apiestudar.api_prodify.application.usecase.localizacao;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.interfaces.controller.LocalizacaoController;
import com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto.EnderecoGeolocalizacaoDTO;
import com.apiestudar.api_prodify.shared.utils.ConsumidorAPI;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObterEnderecoDetalhadoUseCase {

	private static final Logger log = LoggerFactory.getLogger(LocalizacaoController.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

    public ObterEnderecoDetalhadoUseCase() {
    
    }
	
	public EnderecoGeolocalizacaoDTO executar(Double lat, Double lng) {
		return (EnderecoGeolocalizacaoDTO) ConsumidorAPI.chamarAPI("EnderecoDetalhado", lat.toString(), lng.toString());
	}
}
