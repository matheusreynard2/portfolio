package com.apiestudar.api_prodify.application.usecase.fornecedor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Fornecedor;
import com.apiestudar.api_prodify.domain.repository.FornecedorRepository;
import com.apiestudar.api_prodify.interfaces.dto.FornecedorDTO;

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
        long t0 = System.nanoTime();
        
        // Busca a entidade
        Fornecedor fornecedorExistente = fornecedorRepository.buscarFornecedorPorIdEUsuario(id, idUsuario);
        
        // ModelMapper copia tudo automaticamente
        // Cascade + orphanRemoval cuidam dos objetos relacionados
        modelMapper.map(fornecedorDTO, fornecedorExistente);
        
        // Garante que o idUsuario seja preservado e só tem em algumas classes
        fornecedorExistente.setIdUsuario(idUsuario);
        
        // JPA detecta mudanças automaticamente - nem precisa de save()!
        FornecedorDTO fornecededorAtualizadoDTO = modelMapper.map(fornecedorExistente, FornecedorDTO.class);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### ATUALIZAR FORNECEDOR %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return fornecededorAtualizadoDTO;
    }
 }