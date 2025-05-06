package com.apiestudar.api_prodify.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiestudar.api_prodify.entity.Geolocation;
import com.apiestudar.api_prodify.exceptions.GeoLocationException;
import com.apiestudar.api_prodify.pattern.HeaderIpExtractor;
import com.apiestudar.api_prodify.pattern.IpExtractorManager;
import com.apiestudar.api_prodify.service.GeolocationService;
import com.apiestudar.api_prodify.service.UsuarioService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("api/localizacao")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "https://www.sistemaprodify.com",
		"https://www.sistemaprodify.com:8080", "https://www.sistemaprodify.com:80", "https://191.252.38.22:8080",
		"https://191.252.38.22:80", "https://191.252.38.22" }, allowedHeaders = { "*" })
public class LocalizacaoController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GeolocationService geolocationService;

    @ApiOperation(value = "Adiciona novo acesso e contabiliza o total.", notes = "Quando um novo usuário acessa o site, ele registra o IP no banco, e também faz a somatória de todos os IPs que já acessaram o site e exibe no rodapé.")
    @ApiResponse(code = 200, message = "IP registrado, total contabilizado.")
    @GetMapping("/addNovoAcessoIp")
    public ResponseEntity<Long> addNovoAcessoIp(HttpServletRequest req) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.acessar(req));
    }

    @ApiOperation(value = "Localiza informações de endereço do usuário.", notes = "Fornece informações de localização do usuário de acordo com seu IP público.")
    @ApiResponse(code = 200, message = "IP localizado, informações fornecidas.")
    @GetMapping("/localizarIp/{ipAddress}")
    public ResponseEntity<Geolocation> obterGeolocalizacaoUsuario(@PathVariable String ipAddress) throws GeoLocationException {
        Geolocation infoLocalizacao = geolocationService.obterGeolocationByIP(ipAddress);
        return ResponseEntity.ok(infoLocalizacao);
    }
    
    @ApiOperation(value = "Obtém endereço detalhado a partir de coordenadas.", notes = "Utiliza a API de Geocodificação do Google Maps para obter detalhes de endereço.")
    @ApiResponse(code = 200, message = "Endereço detalhado obtido com sucesso.")
    @GetMapping("/enderecoDetalhado")
    public ResponseEntity<Map<String, Object>> obterEnderecoDetalhado(
            @RequestParam double lat, 
            @RequestParam double lng) {
        
    	Map<String, Object> endereco = geolocationService.obterEnderecoDetalhado(lat, lng);
        return ResponseEntity.ok(endereco);
    }
}