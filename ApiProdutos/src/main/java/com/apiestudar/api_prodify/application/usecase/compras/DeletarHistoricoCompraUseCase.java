package com.apiestudar.api_prodify.application.usecase.compras;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarHistoricoCompraUseCase {

    private final HistoricoComprasRepository historicoComprasRepo;

    public DeletarHistoricoCompraUseCase(HistoricoComprasRepository historicoComprasRepo) {
        this.historicoComprasRepo = historicoComprasRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean executar(Long historicoId) {
        long t0 = System.nanoTime();
        if (historicoComprasRepo.buscarHistoricoComprasPorId(historicoId).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }
        historicoComprasRepo.deleteCascadeByHistoricoComprasId(historicoId);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### DELETAR HISTORICO COMPRA %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return true;
    }
}


