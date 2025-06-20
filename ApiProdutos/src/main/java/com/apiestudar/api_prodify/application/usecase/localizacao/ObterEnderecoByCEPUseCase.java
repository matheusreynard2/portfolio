package com.apiestudar.api_prodify.application.usecase.localizacao;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.apiestudar.api_prodify.interfaces.dto.EnderecoFornecedorDTO;
import com.apiestudar.api_prodify.shared.utils.ConsumidorAPI;

@Service
public class ObterEnderecoByCEPUseCase {

    public ObterEnderecoByCEPUseCase(ModelMapper modelMapper) {
    }
    
	public EnderecoFornecedorDTO executar(String cep) {
        return (EnderecoFornecedorDTO) ConsumidorAPI.chamarAPI("EnderecoByCEP", cep, "");
	}   
    
}