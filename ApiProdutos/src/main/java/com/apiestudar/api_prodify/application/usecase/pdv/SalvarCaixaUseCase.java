package com.apiestudar.api_prodify.application.usecase.pdv;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.CaixaItem;
import com.apiestudar.api_prodify.domain.model.VendaCaixa;
import com.apiestudar.api_prodify.domain.repository.VendaCaixaRepository;
import com.apiestudar.api_prodify.interfaces.dto.CaixaItemDTO;
import com.apiestudar.api_prodify.interfaces.dto.VendaCaixaDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import java.util.Collections;

@Service
public class SalvarCaixaUseCase {

    private final VendaCaixaRepository vendaCaixaRepository;
    private final ModelMapper modelMapper;

    public SalvarCaixaUseCase(VendaCaixaRepository vendaCaixaRepository, ModelMapper modelMapper) {
        this.vendaCaixaRepository = vendaCaixaRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long executar(VendaCaixaDTO vendaCaixaDTO) {

        long t0 = System.nanoTime();
        Helper.verificarNull(vendaCaixaDTO);

        // Converte explicitamente os itens do DTO para a collection embutida
        Set<CaixaItem> itensConvertidos = (vendaCaixaDTO.getItens() == null)
            ? Collections.emptySet()
            : vendaCaixaDTO.getItens().stream()
                .map(this::converterItem)
                .collect(Collectors.toSet());

        VendaCaixa vendaCaixaAdicionado;
        if (Helper.maiorZero(vendaCaixaDTO.getId()) && vendaCaixaDTO.getId() != null) {
            // Atualiza a venda existente (mesmo id)
            vendaCaixaAdicionado = vendaCaixaRepository.buscarVendaPorId(vendaCaixaDTO.getId())
                .map(existing -> {
                    // Limpa e aplica novos itens para forçar update da element collection
                    existing.getItens().clear();
                    Helper.verificarNull(itensConvertidos);
                    existing.getItens().addAll(itensConvertidos);
                    existing.setTotalQuantidade(vendaCaixaDTO.getTotalQuantidade());
                    existing.setTotalValor(vendaCaixaDTO.getTotalValor());
                    return vendaCaixaRepository.adicionarVenda(existing);
                })
                .orElseGet(() -> {
                    // Se não encontrou, cria uma nova com os itens convertidos
                    VendaCaixa novo = criarNovaVenda(vendaCaixaDTO, itensConvertidos);
                    return vendaCaixaRepository.adicionarVenda(novo);
                }); // fallback: salva se não encontrar
        } else {
            // Primeira vez: cria nova venda (novo id)
            VendaCaixa novo = criarNovaVenda(vendaCaixaDTO, itensConvertidos);
            vendaCaixaAdicionado = vendaCaixaRepository.adicionarVenda(novo);
        }
        VendaCaixaDTO vendaCaixaDTOAdicionado = modelMapper.map(vendaCaixaAdicionado, VendaCaixaDTO.class);

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### SALVAR VENDA CAIXA %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return vendaCaixaDTOAdicionado.getId();
    }

    private VendaCaixa criarNovaVenda(VendaCaixaDTO vendaCaixaDTO, Set<CaixaItem> itensConvertidos) {
        VendaCaixa novo = new VendaCaixa();
        novo.setIdUsuario(vendaCaixaDTO.getIdUsuario());
        novo.setTotalQuantidade(vendaCaixaDTO.getTotalQuantidade());
        novo.setTotalValor(vendaCaixaDTO.getTotalValor());
        Helper.verificarNull(itensConvertidos);
        novo.getItens().addAll(itensConvertidos);
        return novo;
    }

    private CaixaItem converterItem(CaixaItemDTO dto) {
        CaixaItem item = new CaixaItem();
        item.setIdProduto(dto.getIdProduto());
        item.setQuantidade(dto.getQuantidade());
        item.setTipoPreco(dto.getTipoPreco());
        return item;
    }
}
 