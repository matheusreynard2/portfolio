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
import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.domain.model.Geolocation;
import com.apiestudar.api_prodify.shared.exception.GeoLocationException;
import com.apiestudar.api_prodify.shared.exception.ObterCoordenadasViaCEPException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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

	@ApiOperation(value = "Localiza informações de endereço do usuário.", notes = "Fornece informações de localização do usuário de acordo com seu IP público.")
	@ApiResponse(code = 200, message = "IP localizado, informações fornecidas.")
	@GetMapping("/localizarIp/{ipAddress}")
	public ResponseEntity<Geolocation> obterGeolocalizacaoUsuario(@PathVariable String ipAddress)
			throws GeoLocationException {
		Geolocation infoLocalizacao = obterGeolocationByIP.executar(ipAddress);
		return ResponseEntity.ok(infoLocalizacao);
	}

	@ApiOperation(value = "Obtém endereço detalhado a partir de coordenadas.", notes = "Utiliza a API de Geocodificação do Google Maps para obter detalhes de endereço.")
	@ApiResponse(code = 200, message = "Endereço detalhado obtido com sucesso.")
	@GetMapping("/enderecoDetalhado")
	public ResponseEntity<Map<String, Object>> obterEnderecoDetalhado(@RequestParam double lat,
			@RequestParam double lng) {

		Map<String, Object> endereco = obterEnderecoDetalhado.executar(lat, lng);
		return ResponseEntity.ok(endereco);
	}

	@ApiOperation(value = "Obtém endereço a partir do CEP.", notes = "Utiliza a API ViaCEP para obter informações detalhadas de endereço.")
	@ApiResponse(code = 200, message = "CEP consultado.")
	@GetMapping("/consultarCEP/{cep}")
	public ResponseEntity<EnderecoFornecedor> obterEnderecoViaCEP(@PathVariable String cep)
			throws JsonMappingException, JsonProcessingException {
		EnderecoFornecedor endereco = enderecoByCep.executar(cep);
		return ResponseEntity.ok(endereco);
	}

	@ApiOperation(value = "Obtém coordenadas a partir do CEP.", notes = "Utiliza a API da Google para obter informações de coordenadas latitude e longitude.")
	@ApiResponse(code = 200, message = "Coordenadas consultadas.")
	@GetMapping("/obterCoordenadas/{cep}")
	public ResponseEntity<Map<String, Object>> obterCoordenadasPorCEP(@PathVariable String cep)
			throws JsonMappingException, JsonProcessingException, ObterCoordenadasViaCEPException {
		Map<String, Object> response = coordenadasByCep.executar(cep);
		return response.containsKey("error") ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
				: (response.containsKey("latitude") && response.containsKey("longitude"))
						? ResponseEntity.status(HttpStatus.OK).body(response)
						: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body(Map.of("error", "Resposta inesperada do serviço de geolocalização"));
	}
}