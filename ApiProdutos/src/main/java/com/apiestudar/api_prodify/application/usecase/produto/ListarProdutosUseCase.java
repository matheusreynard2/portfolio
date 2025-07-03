package com.apiestudar.api_prodify.application.usecase.produto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.apiestudar.api_prodify.shared.utils.ListMapperThread;

@Service
public class ListarProdutosUseCase {

	/* ───── dependências & pools ───── */
	private final ProdutoRepository repo;
	private final ExecutorService dbPool; // consultas ao BD
	private final ExecutorService cpuPool; // operações CPU-bound
	private final ModelMapper mapper = new ModelMapper();

	public ListarProdutosUseCase(
			ProdutoRepository repo,
			@Qualifier("dbPool") ExecutorService dbPool,
			@Qualifier("cpuPool") ExecutorService cpuPool) {

		this.repo = repo;
		this.dbPool = dbPool;
		this.cpuPool = cpuPool;
	}

	/**
	 * Busca paginada e mapeia para DTOs em paralelo (duas threads).
	 */
	public CompletableFuture<Page<ProdutoDTO>> executar(Pageable pageable, Long idUsuario) {

		long t0 = System.nanoTime();

		/* 1️⃣ consulta paginada (I/O) */
		CompletableFuture<Page<Produto>> pageFuture = CompletableFuture.supplyAsync(
				() -> repo.listarProdutosByIdUsuario(pageable, idUsuario),
				dbPool);

		/* 2️⃣ completa com JOIN FETCH, depois mapeia em 2 threads */
		CompletableFuture<Page<ProdutoDTO>> dtoFuture = pageFuture.thenComposeAsync(page ->
		/* etapa DB: relê os produtos da página com FETCH JOIN */
		CompletableFuture.supplyAsync(() -> {
			List<Long> ids = page.getContent().stream()
					.map(Produto::getId)
					.collect(Collectors.toList());
			return ids.isEmpty()
					? List.<Produto>of()
					: repo.findAllJoinFetchByIds(ids);
		}, dbPool)
				/* etapa CPU: mapeamento paralelo */
				.thenApplyAsync(full -> {

					/* destino thread-safe */
					List<ProdutoDTO> destino = Collections.synchronizedList(
							new ArrayList<>(full.size()));

					/* divide a lista fonte ao meio */
					int mid = full.size() / 2;
					List<Produto> left = full.subList(0, mid);
					List<Produto> right = full.subList(mid, full.size());

					/* função genérica Produto ➜ DTO */
					Function<Produto, ProdutoDTO> mapperFn = p -> mapper.map(p, ProdutoDTO.class);

					Thread t1 = new ListMapperThread<Produto, ProdutoDTO>(left, destino, mapperFn, "Mapper-L");
					Thread t2 = new ListMapperThread<Produto, ProdutoDTO>(right, destino, mapperFn, "Mapper-R");
					t1.start();
					t2.start();
					try {
						t1.join();
						t2.join();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						throw new IllegalStateException(e);
					}

					return new PageImpl<>(destino, pageable, page.getTotalElements());
				}, cpuPool)

				, dbPool);

		/* métrica */
		return dtoFuture.whenComplete((ok, ex) -> {
			long ns = System.nanoTime() - t0;
			System.out.println("##############################");
			System.out.printf(
					"######## LISTAR PRODUTOS PAGEABLE %d ns ( %d ms) ########%n",
					ns, ns / 1_000_000);
			System.out.println("##############################");
		});
	}
}