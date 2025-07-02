package com.apiestudar.api_prodify.application.usecase.produto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.apiestudar.api_prodify.shared.exception.RegistroNaoEncontradoException;
import com.apiestudar.api_prodify.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarProdutoUseCase {

    private final ProdutoRepository repo;
    private final ExecutorService   cpuPool;   // parsing / mapping
    private final ExecutorService   ioPool;    // leitura de arquivo
    private final ExecutorService   dbPool;    // queries / update
    private final ModelMapper       mapper = new ModelMapper();
    private final ObjectMapper      json   = new ObjectMapper();

    public AtualizarProdutoUseCase(
            ProdutoRepository repo,
            @Qualifier("cpuPool") ExecutorService cpuPool,
            @Qualifier("ioPool")  ExecutorService ioPool,
            @Qualifier("dbPool")  ExecutorService dbPool) {
        this.repo    = repo;
        this.cpuPool = cpuPool;
        this.ioPool  = ioPool;
        this.dbPool  = dbPool;
    }

    public CompletableFuture<ProdutoDTO> executar(
            long id, ProdutoFormDTO form, MultipartFile imagem) {

        /* 1️⃣  Parse JSON (CPU) */
        CompletableFuture<Produto> parsed =
            CompletableFuture.supplyAsync(() -> gerarProduto(form), cpuPool);

        /* 2️⃣  Ler bytes da imagem (I/O) */
        CompletableFuture<byte[]> bytes =
            CompletableFuture.supplyAsync(() -> getBytes(imagem), ioPool);
            
            long t0 = System.nanoTime();

        /* 3️⃣  Mesclar entidade + bytes */
        CompletableFuture<Produto> combinado =
            parsed.thenCombine(bytes, (p, imgBytes) -> { p.setImagem(imgBytes); return p; });

        /* 4️⃣  Atualizar + re-ler com JOIN FETCH (DB) */
        CompletableFuture<Produto> atualizado =
            combinado.thenApplyAsync(prodMem -> {

                // buscar com JOIN FETCH para ter todas as associações carregadas
                Produto existente = repo.findByIdJoinFetch(id)
                                         .orElseThrow(RegistroNaoEncontradoException::new);

                copiarCampos(prodMem, existente);
                repo.salvarProduto(existente);         // UPDATE

                // re-ler novamente já com JOIN FETCH (opcional, mas garante estado)
                return repo.findByIdJoinFetch(id)
                           .orElseThrow(() -> new IllegalStateException("Produto não relido"));
            }, dbPool);

        /* 5️⃣  Mapear DTO (CPU) */
        CompletableFuture<ProdutoDTO> futuro =
            atualizado.thenApplyAsync(prod -> mapper.map(prod, ProdutoDTO.class), cpuPool);

        return futuro.whenComplete((ok, ex) -> {
            long ns = System.nanoTime() - t0;
			System.out.println("##############################");
            System.out.printf("### UPDATE PRODUTO %d ns (≈ %d ms)%n", ns, ns/1_000_000);
			System.out.println("##############################");
        });
    }

    /* helpers */

    private Produto gerarProduto(ProdutoFormDTO form) {
        Helper.verificarNull(form);
        try {
            ProdutoDTO dto = json.readValue(form.getProdutoJson(), ProdutoDTO.class);
            return mapper.map(dto, Produto.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private byte[] getBytes(MultipartFile img) {
        Helper.verificarNull(img);
        try { return img.getBytes(); }
        catch (IOException e) { throw new UncheckedIOException(e); }
    }

    private void copiarCampos(Produto src, Produto dst) {
        dst.setNome(src.getNome());
		dst.setImagem(src.getImagem());
		dst.setPromocao(src.getPromocao());
		dst.setValorTotalDesc(src.getValorTotalDesc());
		dst.setSomaTotalValores(src.getSomaTotalValores());
		dst.setFornecedor(src.getFornecedor());
		dst.setValor(src.getValor());
		dst.setValorTotalFrete(src.getValorTotalFrete());
        dst.setDescricao(src.getDescricao());
        dst.setFrete(src.getFrete());
		dst.setValorInicial(src.getValorInicial());
        dst.setQuantia(src.getQuantia());
        dst.setFreteAtivo(src.getFreteAtivo());
        dst.setValorDesconto(src.getValorDesconto());       
    }
}
