package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarProdutoUseCase {

    private final ProdutoRepository repo;
    private final ExecutorService   cpuPool;   // CPU-bound
    private final ExecutorService   ioPool;    // File/network I/O
    private final ExecutorService   dbPool;    // Banco de dados
    private final ModelMapper       mapper = new ModelMapper();
    private final ObjectMapper      json   = new ObjectMapper();

    public AdicionarProdutoUseCase(
            ProdutoRepository repo,
            @Qualifier("cpuPool") ExecutorService cpuPool,
            @Qualifier("ioPool")  ExecutorService ioPool,
            @Qualifier("dbPool")  ExecutorService dbPool) {
        this.repo    = repo;
        this.cpuPool = cpuPool;
        this.ioPool  = ioPool;
        this.dbPool  = dbPool;
    }

    /** Assíncrono, usando 3 pools otimizados */
    public CompletableFuture<ProdutoDTO> executar(
            ProdutoFormDTO form, MultipartFile imagem) {

        long t0 = System.nanoTime();

        /* ───────────── 1️⃣  Parse JSON (CPU) ───────────── */
        CompletableFuture<Produto> base =
            CompletableFuture.supplyAsync(() -> {
                Helper.verificarNull(form);
                try {
                    ProdutoDTO dto = json.readValue(form.getProdutoJson(), ProdutoDTO.class);
                    return mapper.map(dto, Produto.class); // ainda SEM imagem
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, cpuPool);

        /* ───────────── 2️⃣  Ler bytes da imagem (I/O) ───────────── */
        CompletableFuture<byte[]> bytes =
            CompletableFuture.supplyAsync(() -> {
                Helper.verificarNull(imagem);
                try {
                    return imagem.getBytes();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, ioPool);

        /* ───────────── 3️⃣  Unir entidade + bytes ───────────── */
        CompletableFuture<Produto> completo =
            base.thenCombine(bytes, (p, imgBytes) -> {
                p.setImagem(imgBytes);
                return p;
            });

        /* ───────────── 4️⃣  Salvar + reler com JOIN FETCH (DB) ───────────── */
        CompletableFuture<Produto> salvoCarregado =
            completo.thenApplyAsync(prod -> {

                Produto salvo = repo.salvarProduto(prod); // INSERT
                // RE-LEITURA com JOIN FETCH para trazer coleções carregadas
                return repo.findByIdJoinFetch(salvo.getId())
                           .orElseThrow(() ->
                              new IllegalStateException("Produto não relido"));
            }, dbPool);

        /* ───────────── 5️⃣  Mapear para DTO (CPU) ───────────── */
        CompletableFuture<ProdutoDTO> resultado =
            salvoCarregado.thenApplyAsync(prod -> mapper.map(prod, ProdutoDTO.class), cpuPool);

        /* ───────────── Métrica ───────────── */
        return resultado.whenComplete((ok, ex) -> {
            long ns = System.nanoTime() - t0;
            System.out.println("##############################");
            System.out.printf("### ADD PRODUTO %d ns ( %d ms)%n",
                              ns, ns / 1_000_000);
            System.out.println("##############################");
        });
    }
}
