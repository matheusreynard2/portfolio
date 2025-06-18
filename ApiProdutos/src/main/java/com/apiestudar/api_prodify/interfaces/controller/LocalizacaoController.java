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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

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

	@ApiOperation(value = "Chamda API da ipinfo.io que obtém informações básicas de localização.", notes = "Fornece informações básicas de localização do usuário de acordo com seu IP público através da API da ipinfo.io.")
	@ApiResponse(code = 200, message = "IP localizado, informações fornecidas.")
	@GetMapping("/localizarIp/{ipAddress}")
	public ResponseEntity<GeolocationDTO> obterGeolocalizacaoUsuario(@PathVariable String ipAddress)
			throws GeoLocationException {
		return ResponseEntity.ok(obterGeolocationByIP.executar(ipAddress));
	}

	@ApiOperation(value = "Chama API do Google Maps que obtém endereço.", notes = "Utiliza a API de Geocodificação do Google Maps para obter detalhes de endereço a partir de coordenadas de localização de longitude e latitude e aponta no Google Maps.")
	@ApiResponse(code = 200, message = "Endereço detalhado obtido com sucesso.")
	@GetMapping("/enderecoDetalhado")
	public ResponseEntity<EnderecoGeolocalizacaoDTO> obterEnderecoDetalhado(@RequestParam double lat,
			@RequestParam double lng) {
		return ResponseEntity.ok(obterEnderecoDetalhado.executar(lat, lng));
	}

	@ApiOperation(value = "Chama API ViaCEP que obtém endereço.", notes = "Utiliza a API ViaCEP para obter informações detalhadas de endereço.")
	@ApiResponse(code = 200, message = "CEP consultado.")
	@GetMapping("/consultarCEP/{cep}")
	public ResponseEntity<EnderecoFornecedorDTO> obterEnderecoViaCEP(@PathVariable String cep) {
		return ResponseEntity.ok(enderecoByCep.executar(cep));
	}

	@ApiOperation(value = "Obtém coordenadas a partir do CEP.", notes = "Utiliza a API da Google para obter informações de coordenadas latitude e longitude a partir do CEP fornecido.")
	@ApiResponse(code = 200, message = "Coordenadas consultadas.")
	@GetMapping("/obterCoordenadas/{cep}")
	public ResponseEntity<LatitudeLongitudeDTO> obterCoordenadasPorCEP(@PathVariable String cep) { 
		return ResponseEntity.ok(coordenadasByCep.executar(cep));
	}
	
}