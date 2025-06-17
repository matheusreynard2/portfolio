
package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.EnderecoFornecedor;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterEnderecoByCEPUseCase {

    private final static String API_VIA_CEP = "https://viacep.com.br/ws/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ModelMapper modelMapper;

    public ObterEnderecoByCEPUseCase(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
	public EnderecoFornecedorDTO executar(String cep) {
		
        // CHAMA A "API VIA CEP"
        String response = Unirest.get(API_VIA_CEP + cep + "/json/").header("Accept", "application/json").asString()
				.getBody();

        // TENTA CONVERTER O RESULTADO PARA JSON
        EnderecoFornecedor enderecoFornecedor;
		try {
			enderecoFornecedor = objectMapper.readValue(response, EnderecoFornecedor.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao converter o endereço do CEP da API para JSON", e);
		}

        // RETORNA O ENDEREÇO CONVERTIDO PARA DTO
		return modelMapper.map(enderecoFornecedor, EnderecoFornecedorDTO.class);
	}   
    
}