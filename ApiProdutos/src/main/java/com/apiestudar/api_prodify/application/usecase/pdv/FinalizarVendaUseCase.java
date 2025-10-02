package com.apiestudar.api_prodify.application.usecase.pdv;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.interfaces.dto.VendaCaixaDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.apiestudar.api_prodify.interfaces.ProdutoFeignClient;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
public class FinalizarVendaUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;
    private final ModelMapper modelMapper;
    private final ProdutoFeignClient produtoFeignClient;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final UsuarioRepository usuarioRepository;

    public FinalizarVendaUseCase(
            VendaCaixaRepository vendaCaixaRepository,
            ModelMapper modelMapper,
            ProdutoFeignClient produtoFeignClient,
            UsuarioRepository usuarioRepository) {
        this.vendaCaixaRepository = vendaCaixaRepository;
        this.modelMapper = modelMapper;
        this.produtoFeignClient = produtoFeignClient;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public VendaCaixaDTO executar(Long idVendaCaixa) {

        long t0 = System.nanoTime();

        // validação obrigatória: lança exceção se for null
        Helper.verificarNull(idVendaCaixa);

        // busca obrigatória: lança se não encontrar
        VendaCaixa vendaCaixa = vendaCaixaRepository.buscarVendaPorId(idVendaCaixa)
                .orElseThrow(RegistroNaoEncontradoException::new);

        // salva no histórico e mapeia
        vendaCaixa.setDataVenda(Date.from(Instant.now()));
        VendaCaixa vendaCaixaSalvo= vendaCaixaRepository.adicionarHistorico(vendaCaixa);
        VendaCaixaDTO vendaCaixaSalvoDTO = modelMapper.map(vendaCaixaSalvo, VendaCaixaDTO.class);

        // Atualiza estoque de produtos no serviço de produtos (se houver itens)
        Helper.verificarNull(vendaCaixaSalvo.getItens());
        Long idUsuario = vendaCaixaSalvo.getIdUsuario();

        vendaCaixaSalvo.getItens().forEach(item -> {
            try {
                Long idProduto = item.getIdProduto();

                // busca produto atual e valida não-nulo
                ProdutoDTO produtoAtual = produtoFeignClient.buscarProduto(idProduto, idUsuario).getBody();
                Helper.verificarNull(produtoAtual);

                long quantiaAtual = produtoAtual.getQuantia() == null ? 0L : produtoAtual.getQuantia();
                long quantidadeSaida = item.getQuantidade() == null ? 0L : item.getQuantidade();
                long novaQuantia = Math.max(0L, quantiaAtual - quantidadeSaida);

                // só atualiza se a nova quantidade não for zero (mantém sua regra atual)
                if (Helper.maiorIgualZero(novaQuantia)) {
                    produtoAtual.setQuantia(novaQuantia);
                    String produtoJson = jsonMapper.writeValueAsString(produtoAtual);
                    produtoFeignClient.atualizarProduto(idProduto, produtoJson, null);
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        // Atualiza saldo do usuário somando o total da venda
        Helper.verificarNull(vendaCaixaSalvo.getTotalValor());
        if (Helper.maiorZero(vendaCaixaSalvo.getTotalValor())) {
            usuarioRepository.buscarUsuarioPorId(idUsuario).ifPresent(usuario -> {
                BigDecimal saldoAtual = usuario.getSaldo() == null ? BigDecimal.ZERO : usuario.getSaldo();
                usuario.setSaldo(saldoAtual.add(vendaCaixaSalvo.getTotalValor()));
                usuarioRepository.atualizarUsuario(usuario);
            });
        }

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### HISTORICO VENDAS SALVO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        // sempre retorna um DTO válido aqui
        return vendaCaixaSalvoDTO;
    }
}