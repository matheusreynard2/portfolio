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
		long t0 = System.nanoTime();
		EnderecoFornecedorDTO dto = (EnderecoFornecedorDTO) ConsumidorAPI.chamarAPI("EnderecoByCEP", cep, "");
		long ns = System.nanoTime() - t0;
		System.out.println("##############################");
		System.out.printf("### OBTER ENDERECO POR CEP %d ns ( %d ms)%n", ns, ns / 1_000_000);
		System.out.println("##############################");
		return dto;
	}   
    
}