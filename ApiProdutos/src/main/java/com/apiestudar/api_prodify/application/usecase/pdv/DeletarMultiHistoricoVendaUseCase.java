package com.apiestudar.api_prodify.application.usecase.pdv;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.infrastructure.persistence.jpa.HistoricoVendaJpaRepository;

@Service
public class DeletarMultiHistoricoVendaUseCase {

    private final HistoricoVendaJpaRepository historicoVendaRepo;

    public DeletarMultiHistoricoVendaUseCase(HistoricoVendaJpaRepository historicoVendaRepo) {
        this.historicoVendaRepo = historicoVendaRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean executar(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return false;
        // IMPORTANTE: no front o ID exibido é de VendaCaixa (v.id). Portanto os ids aqui são vendaCaixaIds
        historicoVendaRepo.deleteHistoricosByVendaCaixaIds(ids);
        historicoVendaRepo.deleteItensByVendaCaixaIds(ids);
        historicoVendaRepo.deleteVendaCaixasByIds(ids);
        return true;
    }
}


