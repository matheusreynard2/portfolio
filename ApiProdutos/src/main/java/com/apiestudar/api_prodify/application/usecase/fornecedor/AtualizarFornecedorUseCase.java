package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.application.mapper.FornecedorMapper;
import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarFornecedorUseCase {

    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public AtualizarFornecedorUseCase(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Autowired
    private FornecedorMapper fornecedorMapper;

    @Transactional(rollbackFor = Exception.class)
    public Fornecedor executar(long id, FornecedorDTO fornecedorDTO, Long idUsuario) throws Exception {
        Fornecedor fornecedorAtualizado = fornecedorMapper.toEntity(fornecedorDTO);
        
        // Busca o fornecedor pelo id e idUsuario e lança exceção se não encontrar
        return fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario)
            .map(fornecedorExistente -> {
                // Atualiza todos os atributos do fornecedor existente
                atualizarDadosFornecedor(fornecedorExistente, fornecedorAtualizado);
                fornecedorExistente.setIdUsuario(idUsuario);

                // Salva e retorna o fornecedor atualizado
                return fornecedorRepository.adicionarFornecedor(fornecedorExistente, idUsuario);
            })
            .orElseThrow(RegistroNaoEncontradoException::new);
    }

    // Método auxiliar para atualizar todos os campos do fornecedor
    private void atualizarDadosFornecedor(Fornecedor fornecedorExistente, Fornecedor fornecedorAtualizado) {
        fornecedorExistente.setNome(fornecedorAtualizado.getNome());
        fornecedorExistente.setNrResidencia(fornecedorAtualizado.getNrResidencia());
        fornecedorExistente.setEnderecoFornecedor(fornecedorAtualizado.getEnderecoFornecedor());
    }
} 