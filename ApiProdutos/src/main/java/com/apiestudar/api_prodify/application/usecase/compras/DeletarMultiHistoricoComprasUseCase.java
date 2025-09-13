package com.apiestudar.api_prodify.application.usecase.compras;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;

@Service
public class DeletarMultiHistoricoComprasUseCase {

    private final HistoricoComprasRepository historicoComprasRepo;

    public DeletarMultiHistoricoComprasUseCase(HistoricoComprasRepository historicoComprasRepo) {
        this.historicoComprasRepo = historicoComprasRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean executar(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            historicoComprasRepo.deleteMultiComprasCascadeByHistoricoId(ids);
            return true;
        }
        return false;
    }
}


