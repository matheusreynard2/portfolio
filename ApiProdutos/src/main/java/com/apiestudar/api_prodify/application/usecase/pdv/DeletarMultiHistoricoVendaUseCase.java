package com.apiestudar.api_prodify.application.usecase.pdv;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.infrastructure.persistence.jpa.VendaCaixaJpaRepository;

@Service
public class DeletarMultiHistoricoVendaUseCase {

    private final VendaCaixaJpaRepository vendaCaixaRepo;

    public DeletarMultiHistoricoVendaUseCase(VendaCaixaJpaRepository vendaCaixaRepo) {
        this.vendaCaixaRepo = vendaCaixaRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean executar(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return false;
        vendaCaixaRepo.deleteItensByVendaCaixaIds(ids);
        vendaCaixaRepo.deleteVendaCaixasByIds(ids);
        return true;
    }
}


