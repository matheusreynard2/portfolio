package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class AdicionarFornecedorUseCase {

    @Autowired
    private ModelMapper modelMapper;
    private final FornecedorRepository fornecedorRepository;

    public AdicionarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public FornecedorDTO executar(FornecedorDTO fornecedorDTO, Long idUsuario) {
        Helper.verificarNull(fornecedorDTO);
        Fornecedor fornecedor = modelMapper.map(fornecedorDTO, Fornecedor.class);
        Fornecedor fornecedorAdicionado = fornecedorRepository.adicionarFornecedor(fornecedor, idUsuario);
        return modelMapper.map(fornecedorAdicionado, FornecedorDTO.class);
    }
}
