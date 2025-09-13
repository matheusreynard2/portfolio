package com.apiestudar.api_prodify.application.usecase.compras;

import com.apiestudar.api_prodify.interfaces.dto.CompraDTO;
import com.apiestudar.api_prodify.interfaces.dto.HistoricoComprasDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.apiestudar.api_prodify.interfaces.ProdutoFeignClient;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Compra;
import com.apiestudar.api_prodify.domain.model.HistoricoCompras;
import com.apiestudar.api_prodify.domain.repository.CompraRepository;
import com.apiestudar.api_prodify.domain.repository.HistoricoComprasRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarCompraUseCase {

	private CompraRepository repoCompras;
	private HistoricoComprasRepository historicoComprasRepo;
	@Autowired
    private ModelMapper modelMapper;
	@Autowired
	private ProdutoFeignClient produtoFeignClient;
	private final ObjectMapper jsonMapper = new ObjectMapper();

	public AdicionarCompraUseCase(CompraRepository repoCompras, HistoricoComprasRepository historicoComprasRepo) {
		this.repoCompras = repoCompras;
		this.historicoComprasRepo = historicoComprasRepo;
	}

	@Transactional(rollbackFor = Exception.class)
	public HistoricoComprasDTO executar(List<CompraDTO> comprasDTO) {
		long t0 = System.nanoTime();
		Helper.verificarNull(comprasDTO);
		if (comprasDTO.isEmpty()) {
			throw new IllegalArgumentException("A lista de compras não pode estar vazia.");
		}
	
		// Garante consistência do usuário (opcional: lance exceção se IDs divergirem)
		Long idUsuario = comprasDTO.get(0).getIdUsuario();
	
		Long quantidadeTotal = 0L;
		BigDecimal valorTotal = BigDecimal.ZERO;
		Set<Compra> comprasSalvas = new HashSet<>();
	
		for (CompraDTO compraDTO : comprasDTO) {
			Compra compra = modelMapper.map(compraDTO, Compra.class);
			Compra compraSalva = repoCompras.adicionarCompra(compra, compraDTO.getIdUsuario());
	
			comprasSalvas.add(compraSalva);
			// Soma a partir do que foi efetivamente persistido
			quantidadeTotal += compraSalva.getQuantidadeComprada();
			if (compraSalva.getValorTotalCompra() != null) {
				valorTotal = valorTotal.add(compraSalva.getValorTotalCompra());
			}
		}
	
		HistoricoCompras historicoCompras = new HistoricoCompras();
		historicoCompras.setIdUsuario(idUsuario);
		historicoCompras.setCompras(comprasSalvas);
		historicoCompras.setQuantidadeTotal(quantidadeTotal);
		historicoCompras.setValorTotal(valorTotal);
		historicoCompras.setDataCompra(Date.from(Instant.now()));
	
		historicoComprasRepo.adicionarHistoricoCompras(historicoCompras);

		// Atualiza estoque de produtos no serviço de produtos (espelhando FinalizarVendaUseCase)
		Helper.verificarNull(historicoCompras.getCompras());
		Long idUsuarioProdutos = historicoCompras.getIdUsuario();
		historicoCompras.getCompras().forEach(compra -> {
			try {
				Long idProduto = compra.getProduto().getId();
				ProdutoDTO produtoAtual = produtoFeignClient.buscarProduto(idProduto, idUsuarioProdutos).getBody();
				Helper.verificarNull(produtoAtual);
				long quantiaAtual = produtoAtual.getQuantia() == null ? 0L : produtoAtual.getQuantia();
				long quantidadeEntrada = compra.getQuantidadeComprada() == null ? 0L : compra.getQuantidadeComprada();
				long novaQuantia = Math.max(0L, quantiaAtual + quantidadeEntrada);
				if (Helper.maiorIgualZero(novaQuantia)) {
					produtoAtual.setQuantia(novaQuantia);
					String produtoJson = jsonMapper.writeValueAsString(produtoAtual);
					produtoFeignClient.atualizarProduto(idProduto, produtoJson, null);
				}
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
	
		HistoricoComprasDTO historicoComprasDTO = modelMapper.map(historicoCompras, HistoricoComprasDTO.class);
	
		long ns = System.nanoTime() - t0;
		System.out.println("##############################");
		System.out.printf("## ADICIONADO COMPRA %d ns ( %d ms)%n", ns, ns / 1_000_000);
		System.out.println("##############################");
	
		return historicoComprasDTO;
	}
	
	
}