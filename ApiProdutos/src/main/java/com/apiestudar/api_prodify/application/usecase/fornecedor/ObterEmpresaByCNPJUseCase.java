package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.apiestudar.api_prodify.interfaces.dto.brasilapi_dto.DadosEmpresaDTO;
import com.apiestudar.api_prodify.shared.utils.ConsumidorAPI;

@Service
public class ObterEmpresaByCNPJUseCase {

    public ObterEmpresaByCNPJUseCase(ModelMapper modelMapper) {
    }
    
    public DadosEmpresaDTO executar(String cnpj) {
        long t0 = System.nanoTime();
        DadosEmpresaDTO dadosEmpresaDTO = (DadosEmpresaDTO) ConsumidorAPI.chamarAPI("EmpresaByCNPJ", cnpj, "");
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### OBTER EMPRESA CNPJ %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return dadosEmpresaDTO;
    }   
}