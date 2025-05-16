
package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterEnderecoByCEPUseCase {

    private final static String API_VIA_CEP = "https://viacep.com.br/ws/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ObterEnderecoByCEPUseCase( ) {
    
    }
    
	public EnderecoFornecedor executar(String cep) throws JsonMappingException, JsonProcessingException {
		String response = Unirest.get(API_VIA_CEP + cep + "/json/").header("Accept", "application/json").asString()
				.getBody();
		return objectMapper.readValue(response, EnderecoFornecedor.class);
	}   
    
}