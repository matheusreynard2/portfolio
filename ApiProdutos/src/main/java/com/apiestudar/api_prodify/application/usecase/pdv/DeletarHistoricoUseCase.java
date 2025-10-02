package com.apiestudar.api_prodify.application.usecase.pdv;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class DeletarHistoricoUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;

    public DeletarHistoricoUseCase(VendaCaixaRepository vendaCaixaRepository) {
        this.vendaCaixaRepository = vendaCaixaRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean executar(Long vendaCaixaId) {
        long t0 = System.nanoTime();
        if (vendaCaixaRepository.findHistoricoByVendaCaixaId(vendaCaixaId).isEmpty()) {
            throw new RegistroNaoEncontradoException();
        }
        vendaCaixaRepository.deleteCascadeByVendaCaixaId(vendaCaixaId);
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### DELETAR HISTORICO VENDA %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return true;
    }
}


