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
import com.apiestudar.api_prodify.interfaces.ProdutoFeignClient;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FinalizarVendaUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;
    private final ModelMapper modelMapper;
    private final ProdutoFeignClient produtoFeignClient;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public FinalizarVendaUseCase(
            VendaCaixaRepository vendaCaixaRepository,
            ModelMapper modelMapper,
            ProdutoFeignClient produtoFeignClient) {
        this.vendaCaixaRepository = vendaCaixaRepository;
        this.modelMapper = modelMapper;
        this.produtoFeignClient = produtoFeignClient;
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

        // Atualiza estoque de produtos no serviço de produtos
        if (vendaCaixaHistorico.getItens() != null && !vendaCaixaHistorico.getItens().isEmpty()) {
            Long idUsuario = vendaCaixaHistorico.getIdUsuario();
            vendaCaixaHistorico.getItens().forEach(item -> {
                try {
                    Long idProduto = item.getIdProduto();
                    // Busca produto atual
                    ProdutoDTO produtoAtual = produtoFeignClient.buscarProduto(idProduto, idUsuario).getBody();
                    Helper.verificarNull(produtoAtual);
                    long novaQuantia = Math.max(0L, (produtoAtual.getQuantia() == null ? 0L : produtoAtual.getQuantia()) - (item.getQuantidade() == null ? 0L : item.getQuantidade()));
                    if (novaQuantia >= 0L) {
                        // Atualiza apenas a quantidade (mantendo demais campos)
                        produtoAtual.setQuantia(novaQuantia);
                        String produtoJson = jsonMapper.writeValueAsString(produtoAtual);
                        produtoFeignClient.atualizarProduto(idProduto, produtoJson, null);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### HISTORICO VENDAS SALVO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return vcHistoricoSalvo;

        }
    }

}
