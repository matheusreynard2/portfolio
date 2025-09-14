package com.prodify.produto_service.application.usecase.produto;

import org.springframework.transaction.annotation.Transactional;

import com.prodify.produto_service.adapter.in.web.dto.ProdutoHistoricoBloqueioDTO;      
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.shared.exception.ProdutosPossuemHistoricoRelacionadoException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeletarMultiProdutosUseCase {

	private final ProdutoRepository prodRepo;

	public DeletarMultiProdutosUseCase(ProdutoRepository prodRepo) {
		this.prodRepo = prodRepo;
    }

	@Transactional(rollbackFor = Exception.class)
    public Boolean executar(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) return false;

    List<Long> idsLong = ids.stream().map(Integer::longValue).toList();

    // Busca, em lote, quais ids têm histórico de venda/compra
    List<Long> vendaIds  = prodRepo.findProdutoIdsComHistoricoVenda(idsLong);
    List<Long> compraIds = prodRepo.findProdutoIdsComHistoricoCompra(idsLong);

    Set<Long> bloqueados = new LinkedHashSet<>();
    if (vendaIds != null)  bloqueados.addAll(vendaIds);
    if (compraIds != null) bloqueados.addAll(compraIds);

    if (!bloqueados.isEmpty()) {
        // Pegar nomes para a resposta
        Map<Long,String> nomes = prodRepo.findIdENomeByIds(new ArrayList<>(bloqueados)).stream()
            .collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> (String) r[1]));

        List<ProdutoHistoricoBloqueioDTO> lista = new ArrayList<>();
        for (Long pid : bloqueados) {
        String tipo = (vendaIds != null && vendaIds.contains(pid)) ? "VENDA" : "COMPRA";
        lista.add(new ProdutoHistoricoBloqueioDTO(pid, nomes.getOrDefault(pid, ""), tipo));
        }
        throw new ProdutosPossuemHistoricoRelacionadoException(lista);
    }

    // Nenhum com histórico -> deleta em lote
    prodRepo.deleteByIds(idsLong);
    return true;
    }
}