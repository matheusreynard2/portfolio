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
        long t0 = System.nanoTime();
        if (ids != null && !ids.isEmpty()) {
            historicoComprasRepo.deleteMultiComprasCascadeByHistoricoId(ids);
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### DELETAR MULTI HISTORICO COMPRAS %d ns ( %d ms)%n", ns, ns / 1_000_000);
            System.out.println("##############################");
            return true;
        }
        return false;
    }
}


