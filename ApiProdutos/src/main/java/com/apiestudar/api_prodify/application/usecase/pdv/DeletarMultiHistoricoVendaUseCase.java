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
        long t0 = System.nanoTime();
        if (ids == null || ids.isEmpty()) return false;
        vendaCaixaRepo.deleteItensByVendaCaixaIds(ids);
        vendaCaixaRepo.deleteVendaCaixasByIds(ids);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### DELETAR MULTI HISTORICO VENDA %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return true;
    }
}


