package com.prodify.produto_service.application.usecase.produto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.PaginatedResponseDTO;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.utils.PartirListaEmDuasThread;

public class ListarProdutosUseCase {

	private final ProdutoRepository repo;
	private final ExecutorService dbPool;
	private final ModelMapper mapper = new ModelMapper();

	public ListarProdutosUseCase(ProdutoRepository repo, ExecutorService dbPool) {
		this.repo = repo;
		this.dbPool = dbPool;
	}

	public CompletableFuture<PaginatedResponseDTO<ProdutoDTO>> executar(Pageable pageable, Long idUsuario) {
		long t0 = System.nanoTime();

		// 1️⃣ Busca paginada original (Page<Produto>)
		return CompletableFuture.supplyAsync(() -> repo.listarProdutosByIdUsuario(pageable, idUsuario), dbPool)
				.thenComposeAsync(page -> {
					List<Long> ids = page.getContent().stream().map(Produto::getId).toList();

					// 2️⃣ Recarrega os produtos com JOIN FETCH
					return CompletableFuture.supplyAsync(() -> {
						List<Produto> produtosComJoin = ids.isEmpty() ? List.of() : repo.findAllJoinFetchByIds(ids);

						// 3️⃣ Mapeamento paralelo em duas threads
						List<ProdutoDTO> destino = Collections.synchronizedList(new ArrayList<>(produtosComJoin.size()));
						int mid = produtosComJoin.size() / 2;
						List<Produto> left = produtosComJoin.subList(0, mid);
						List<Produto> right = produtosComJoin.subList(mid, produtosComJoin.size());

						Function<Produto, ProdutoDTO> mapperFn = p -> mapper.map(p, ProdutoDTO.class);

						Thread t1 = new PartirListaEmDuasThread<>(left, destino, mapperFn, "Mapper-L");
						Thread t2 = new PartirListaEmDuasThread<>(right, destino, mapperFn, "Mapper-R");

						t1.start();
						t2.start();

						try {
							t1.join();
							t2.join();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							throw new IllegalStateException("Erro ao mapear produtos em paralelo", e);
						}

						// 4️⃣ Retorno com DTO paginado customizado
						return new PaginatedResponseDTO<>(
								destino,
								page.getTotalElements(),
								page.getTotalPages(),
								pageable.getPageSize(),
								pageable.getPageNumber()
						);
					}, dbPool);
				}, dbPool)
				.whenComplete((ok, ex) -> {
					long ns = System.nanoTime() - t0;
					System.out.println("##############################");
					System.out.printf("### LISTAR PRODUTOS %d ns ( %d ms)%n", ns, ns / 1_000_000);
					System.out.println("##############################");
				});
	}
}
