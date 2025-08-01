package com.apiestudar.api_prodify.interfaces.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.application.usecase.localizacao.ObterCoordenadasByCEPUseCase;
import com.apiestudar.api_prodify.application.usecase.localizacao.ObterEnderecoByCEPUseCase;
import com.apiestudar.api_prodify.application.usecase.localizacao.ObterEnderecoDetalhadoUseCase;
import com.apiestudar.api_prodify.application.usecase.localizacao.ObterGeolocationByIPUseCase;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.apiestudar.api_prodify.interfaces.dto.GeolocationDTO;
import com.apiestudar.api_prodify.interfaces.dto.LatitudeLongitudeDTO;
import com.apiestudar.api_prodify.interfaces.dto.geolocation_api_dto.EnderecoGeolocalizacaoDTO;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/localizacao")
public class LocalizacaoController {

	@Autowired
	private ObterEnderecoByCEPUseCase enderecoByCep;
	@Autowired
	private ObterGeolocationByIPUseCase obterGeolocationByIP;
	@Autowired
	private ObterEnderecoDetalhadoUseCase obterEnderecoDetalhado;
	@Autowired
	private ObterCoordenadasByCEPUseCase coordenadasByCep;

	@Operation(summary = "Chamda API da ipinfo.io que obtém informações básicas de localização.", description = "Fornece informações básicas de localização do usuário de acordo com seu IP público através da API da ipinfo.io.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "IP localizado, informações fornecidas.")
	})
	@GetMapping("/localizarIp/{ipAddress}")
	public ResponseEntity<GeolocationDTO> obterGeolocalizacaoUsuario(@PathVariable String ipAddress)
			throws GeoLocationException {
		return ResponseEntity.ok(obterGeolocationByIP.executar(ipAddress));
	}

	@Operation(summary = "Chama API do Google Maps que obtém endereço.", description = "Utiliza a API de Geocodificação do Google Maps para obter detalhes de endereço a partir de coordenadas de localização de longitude e latitude e aponta no Google Maps.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Endereço detalhado obtido com sucesso.")
	})
	@GetMapping("/enderecoDetalhado")
	public ResponseEntity<EnderecoGeolocalizacaoDTO> obterEnderecoDetalhado(@RequestParam double lat,
			@RequestParam double lng) {
		return ResponseEntity.ok(obterEnderecoDetalhado.executar(lat, lng));
	}

	@Operation(summary = "Chama API ViaCEP que obtém endereço.", description = "Utiliza a API ViaCEP para obter informações detalhadas de endereço.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "CEP consultado.")
	})
	@GetMapping("/consultarCEP/{cep}")
	public ResponseEntity<EnderecoFornecedorDTO> obterEnderecoViaCEP(@PathVariable String cep) {
		return ResponseEntity.ok(enderecoByCep.executar(cep));
	}

	@Operation(summary = "Obtém coordenadas a partir do CEP.", description = "Utiliza a API da Google para obter informações de coordenadas latitude e longitude a partir do CEP fornecido.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Coordenadas consultadas.")
	})
	@GetMapping("/obterCoordenadas/{cep}")
	public ResponseEntity<LatitudeLongitudeDTO> obterCoordenadasPorCEP(@PathVariable String cep) { 
		return ResponseEntity.ok(coordenadasByCep.executar(cep));
	}
	
}