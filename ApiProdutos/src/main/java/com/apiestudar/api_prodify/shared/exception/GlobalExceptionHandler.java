package com.apiestudar.api_prodify.shared.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<Object> handleRegistroNaoEncontradoException(RegistroNaoEncontradoException ex) {
        log.error("Erro ao processar registro - Registro não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(ParametroInformadoNullException.class)
    public ResponseEntity<Object> handleParametroInformadoNullException(ParametroInformadoNullException ex) {
    	log.error("Erro ao adicionar registro - Param não informado: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex) {
    	log.error("Erro em formatacao de numero: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
    	log.error("Erro generico: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    
    @ExceptionHandler(GeoLocationException.class)
    public ResponseEntity<Object> handleGeoLocationException(GeoLocationException ex) {
    	log.error("Erro ao obter geolocalizacao pelo IP: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<Object> handleJsonMappingException(JsonMappingException ex) {
    	log.error("Erro ao utilizar Mapper com JSON: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex) {
    	log.error("Erro ao processar JSON: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(ObterCoordenadasViaCEPException.class)
    public ResponseEntity<Map<String, String>> handleObterCoordenadasViaCEPException(ObterCoordenadasViaCEPException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("erro", "ERRO_OBTER_CEP");
        errorResponse.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(LoginJaExisteException.class)
    public ResponseEntity<Map<String, String>> handleLoginJaExisteException(LoginJaExisteException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("erro", "LOGIN_JA_EXISTE");
        errorResponse.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredenciaisInvalidasException(CredenciaisInvalidasException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("erro", "CREDENCIAIS_INVALIDAS");
        errorResponse.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}