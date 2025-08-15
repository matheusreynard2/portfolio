package com.apiestudar.api_prodify.application.usecase.pdv;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;

@Service
public class ListarHistoricoUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;

    public ListarHistoricoUseCase(VendaCaixaRepository vendaCaixaRepository, ModelMapper modelMapper) {
        this.vendaCaixaRepository = vendaCaixaRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<VendaCaixa> executar() {

        long t0 = System.nanoTime();

        List<VendaCaixa> vendasComHistorico = vendaCaixaRepository.listarVendasComHistorico();

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("## LISTAR HISTORICO VENDAS %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return vendasComHistorico;
    }

}
