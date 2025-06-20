package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class AtualizarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;

    public AtualizarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    public FornecedorDTO executar(long id, FornecedorDTO fornecedorDTO, Long idUsuario) {
        Fornecedor fornecedorAtualizado = modelMapper.map(fornecedorDTO, Fornecedor.class);
        
        // Busca o fornecedor pelo id e idUsuario e lança exceção se não encontrar
        Fornecedor fornecedorSalvo = fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario)
            .map(fornecedorExistente -> {
                // Atualiza todos os atributos do fornecedor existente
                atualizarDadosFornecedor(fornecedorExistente, fornecedorAtualizado);
                fornecedorExistente.setIdUsuario(idUsuario);

                // Salva e retorna o fornecedor atualizado
                return fornecedorRepository.adicionarFornecedor(fornecedorExistente, idUsuario);
            })
            .orElseThrow(RegistroNaoEncontradoException::new);
        
        return modelMapper.map(fornecedorSalvo, FornecedorDTO.class);
    }

    // Método auxiliar para atualizar todos os campos do fornecedor
    private void atualizarDadosFornecedor(Fornecedor fornecedorExistente, Fornecedor fornecedorAtualizado) {
        fornecedorExistente.setNome(fornecedorAtualizado.getNome());
        fornecedorExistente.setEnderecoFornecedor(fornecedorAtualizado.getEnderecoFornecedor());
    }
} 