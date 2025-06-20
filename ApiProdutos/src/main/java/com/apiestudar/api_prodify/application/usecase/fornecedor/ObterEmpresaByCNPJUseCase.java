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
        return (DadosEmpresaDTO) ConsumidorAPI.chamarAPI("EmpresaByCNPJ", cnpj, "");
    }   
}