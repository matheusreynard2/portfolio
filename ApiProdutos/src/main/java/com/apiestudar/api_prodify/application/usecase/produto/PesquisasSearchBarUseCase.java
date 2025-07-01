package com.apiestudar.api_prodify.application.usecase.produto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.C;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.infrastructure.persistence.jpa.utils.ProdutoSpecification;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;

@Service
public class PesquisasSearchBarUseCase {

    private final ProdutoRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final ExecutorService   ioPool;   // consultas DB
    private final ExecutorService   cpuPool;  // map-to-DTO
    private final ModelMapper       mapper = new ModelMapper();

    public PesquisasSearchBarUseCase(
            ProdutoRepository repo,
            UsuarioRepository usuarioRepo,
            @Qualifier("ioPool")  ExecutorService ioPool,
            @Qualifier("cpuPool") ExecutorService cpuPool) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.ioPool      = ioPool;
        this.cpuPool     = cpuPool;
    }

    /** Pesquisa por vários parâmetros; tudo assíncrono */
    public CompletableFuture<List<ProdutoDTO>> efetuarPesquisa(
            long   idUsuario,
            Long   idProd,
            String nomeProd,
            String nomeFornecedor,
            Long   valorInicial) {

        long t0 = System.nanoTime();

        /*───────── monta Specification ─────────*/
        Specification<Produto> spec = Specification
                .where(ProdutoSpecification.userId(idUsuario))
                .and(ProdutoSpecification.idEquals(idProd))
                .and(ProdutoSpecification.nomeLike(nomeProd))
                .and(ProdutoSpecification.nomeFornecedorLike(nomeFornecedor))
                .and(ProdutoSpecification.valorInicialEquals(valorInicial));

        CompletableFuture<List<ProdutoDTO>> futuro =
            CompletableFuture.supplyAsync(() -> {
                // valida o usuário antes da query
                if (usuarioRepo.buscarUsuarioPorId(idUsuario).isEmpty())
                    throw new RegistroNaoEncontradoException();

                // 1. Busca os produtos filtrados (apenas IDs)
                List<Produto> produtosFiltrados = repo.findAll(spec);
                List<Long> ids = produtosFiltrados.stream().map(Produto::getId).collect(Collectors.toList());
                if (ids.isEmpty()) return List.of();

                // 2. Busca os produtos completos com JOIN FETCH
                List<Produto> produtosCompletos = repo.findAllJoinFetchByIds(ids);

                // 3. Mapping para DTO
                return produtosCompletos.stream()
                        .map(p -> mapper.map(p, ProdutoDTO.class))
                        .distinct()
                        .collect(Collectors.toList());
            }, ioPool)
            .thenApplyAsync(r -> (List<ProdutoDTO>) r, cpuPool);

        return futuro.whenComplete((r, ex) -> {
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### PESQUISAR PRODUTOS %d ns (≈ %d ms)%n",
                              ns, ns / 1_000_000);
            System.out.println("##############################");
        });
    }
}
