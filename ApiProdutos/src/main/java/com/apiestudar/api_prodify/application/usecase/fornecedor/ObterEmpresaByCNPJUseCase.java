package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.brasilapi.DadosEmpresa;
import com.apiestudar.api_prodify.interfaces.dto.brasilapi.DadosEmpresaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;

@Service
public class ObterEmpresaByCNPJUseCase {

    private final static String API_BRASIL_API = "https://brasilapi.com.br/api/cnpj/v1/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ModelMapper modelMapper;

    public ObterEmpresaByCNPJUseCase(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
    public DadosEmpresaDTO executar(String cnpj) {
        
        // Remove formatação do CNPJ (deixa apenas números)
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
        
        // Valida se o CNPJ tem 14 dígitos
        if (cnpjLimpo.length() != 14) {
            throw new RuntimeException("CNPJ deve conter exatamente 14 dígitos");
        }
        
        // CHAMA A "BRASIL API"
        String response = Unirest.get(API_BRASIL_API + cnpjLimpo)
                .header("Accept", "application/json")
                .asString()
                .getBody();

        // TENTA CONVERTER O RESULTADO PARA JSON
        DadosEmpresa dadosEmpresa;
        try {
            dadosEmpresa = objectMapper.readValue(response, DadosEmpresa.class);
        } catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao converter o endereço do CEP da API para JSON", e);
		}

        // RETORNA OS DADOS CONVERTIDOS PARA DTO
        return modelMapper.map(dadosEmpresa, DadosEmpresaDTO.class);
    }   
}