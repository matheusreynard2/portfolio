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
        if (historicoComprasRepo.buscarHistoricoComprasPorId(historicoId).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }
        historicoComprasRepo.deleteCascadeByHistoricoComprasId(historicoId);
        return true;
    }
}


