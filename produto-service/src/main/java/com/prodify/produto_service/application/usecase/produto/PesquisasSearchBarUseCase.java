package com.prodify.produto_service.application.usecase.produto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.UsuarioRepository;
import com.prodify.produto_service.infrastructure.persistence.jpa.specification.ProdutoSpecification;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;

public class PesquisasSearchBarUseCase {

    private final ProdutoRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final ExecutorService   dbPool;     // chamadas a BD
    private final ModelMapper       mapper = new ModelMapper();

    public PesquisasSearchBarUseCase(
            ProdutoRepository repo,
            UsuarioRepository usuarioRepo,
            ExecutorService dbPool) {

        this.repo        = repo;
        this.usuarioRepo = usuarioRepo;
        this.dbPool      = dbPool;
    }

    /** Pesquisa por vários parâmetros — cadeia totalmente assíncrona. */
    public CompletableFuture<List<ProdutoDTO>> efetuarPesquisa(
            long   idUsuario,
            Long   idProd,
            String nomeProd,
            String nomeFornecedor,
            Long   valorInicial) {

        long t0 = System.nanoTime();

        /*───────── 1  Valida usuário + cria Specification (DB pool) ─────────*/
        CompletableFuture<Specification<Produto>> specFuture =
            CompletableFuture.supplyAsync(() -> {

                /* ← consulta em BD dentro do mesmo pool */
                if (usuarioRepo.buscarUsuarioPorId(idUsuario).isEmpty()) {
                    throw new RegistroNaoEncontradoException();
                }

                return Specification
                        .where(ProdutoSpecification.userId(idUsuario))
                        .and(ProdutoSpecification.idEquals(idProd))
                        .and(ProdutoSpecification.nomeLike(nomeProd))
                        .and(ProdutoSpecification.nomeFornecedorLike(nomeFornecedor))
                        .and(ProdutoSpecification.valorInicialEquals(valorInicial));

            }, dbPool);

        /*───────── 2  Executa a query filtrada (DB) ─────────*/
        CompletableFuture<List<Produto>> filtradosFuture =
            specFuture.thenComposeAsync(
                spec -> CompletableFuture.supplyAsync(() -> repo.findAll(spec), dbPool),
                dbPool);

        /*───────── 3️ Extrai os IDs (CPU) ─────────*/
        CompletableFuture<List<Long>> idsFuture =
            filtradosFuture.thenApplyAsync(
                list -> list.stream().map(Produto::getId).collect(Collectors.toList()),
                    dbPool);

        /*───────── 4️ JOIN FETCH pelos IDs (DB) ─────────*/
        CompletableFuture<List<Produto>> completosFuture =
            idsFuture.thenComposeAsync(ids -> {
                if (ids.isEmpty()) {
                    return CompletableFuture.completedFuture(List.of());
                }
                return CompletableFuture.supplyAsync(() -> repo.findAllJoinFetchByIds(ids), dbPool);
            }, dbPool);

        /*───────── 5️ Map → DTO + distinct (CPU) ─────────*/
        CompletableFuture<List<ProdutoDTO>> dtoFuture =
            completosFuture.thenApplyAsync(list ->
                    list.stream()
                        .map(p -> mapper.map(p, ProdutoDTO.class))
                        .distinct()
                        .collect(Collectors.toList()),
                    dbPool);

        /*───────── Métrica ─────────*/
        return dtoFuture.whenComplete((r, ex) -> {
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### PESQUISAR PRODUTOS %d ns (≈ %d ms)%n",
                              ns, ns / 1_000_000);
            System.out.println("##############################");
        });
    }
}
