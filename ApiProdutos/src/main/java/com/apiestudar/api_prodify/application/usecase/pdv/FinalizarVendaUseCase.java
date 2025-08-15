package com.apiestudar.api_prodify.application.usecase.pdv;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.interfaces.dto.VendaCaixaDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;

@Service
public class FinalizarVendaUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;
    private final ModelMapper modelMapper;

    public FinalizarVendaUseCase(VendaCaixaRepository vendaCaixaRepository, ModelMapper modelMapper) {
        this.vendaCaixaRepository = vendaCaixaRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public VendaCaixaDTO executar(Long idVendaCaixa) {
            
        long t0 = System.nanoTime();
        Helper.verificarNull(idVendaCaixa);

		if (vendaCaixaRepository.buscarVendaPorId(idVendaCaixa).isEmpty()) {
			throw new RegistroNaoEncontradoException();
		} else {

        Optional<VendaCaixa> vendaCaixaSalvo = vendaCaixaRepository.buscarVendaPorId(idVendaCaixa);
        VendaCaixa vendaCaixaHistorico = vendaCaixaRepository.adicionarHistorico(vendaCaixaSalvo.get());
        VendaCaixaDTO vcHistoricoSalvo = modelMapper.map(vendaCaixaHistorico, VendaCaixaDTO.class);

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### HISTORICO VENDAS SALVO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return vcHistoricoSalvo;

        }
    }

}
