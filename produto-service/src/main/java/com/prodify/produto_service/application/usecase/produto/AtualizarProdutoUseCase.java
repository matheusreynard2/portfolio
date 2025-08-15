package com.prodify.produto_service.application.usecase.produto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AtualizarProdutoUseCase {

    private final ProdutoRepository repo;
    private final ExecutorService dbPool;
    private final ModelMapper mapper = new ModelMapper();
    private final ObjectMapper json = new ObjectMapper();

    public AtualizarProdutoUseCase(ProdutoRepository repo, ExecutorService dbPool) {
        this.repo = repo;
        this.dbPool = dbPool;
    }

    public CompletableFuture<ProdutoDTO> executar(
            long id, String produtoJson, MultipartFile imagem) {

        long t0 = System.nanoTime();

        // 1️⃣ Parse do JSON
        CompletableFuture<Produto> parsed = CompletableFuture.supplyAsync(() -> {
            try {
                ProdutoDTO dto = json.readValue(produtoJson, ProdutoDTO.class);
                return mapper.map(dto, Produto.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, dbPool);

        // 2️⃣ Leitura da imagem (opcional)
        CompletableFuture<byte[]> bytes = CompletableFuture.supplyAsync(() -> {
            if (imagem == null || imagem.isEmpty()) return null;
            try {
                return imagem.getBytes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, dbPool);

        // 3️⃣ Mescla os dados
        CompletableFuture<Produto> combinado = parsed.thenCombine(bytes, (produtoDoJson, imagemBytes) -> {
            if (imagemBytes != null) produtoDoJson.setImagem(imagemBytes);
            return produtoDoJson;
        });

        // 4️⃣ Atualiza entidade
        CompletableFuture<Produto> atualizado = combinado.thenApplyAsync(prodMem -> {
            Produto existente = repo.findByIdJoinFetch(id)
                    .orElseThrow(() -> new RegistroNaoEncontradoException());

            copiarCampos(prodMem, existente);
            repo.salvarProduto(existente);

            return repo.findByIdJoinFetch(id)
                    .orElseThrow(() -> new IllegalStateException("Produto não relido após atualização"));
        }, dbPool);

        // 5️⃣ Mapear para DTO
        return atualizado.thenApplyAsync(prod -> mapper.map(prod, ProdutoDTO.class), dbPool)
                .whenComplete((ok, ex) -> {
                    long ns = System.nanoTime() - t0;
                    System.out.println("##############################");
                    System.out.printf("### UPDATE DESCONTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
                    System.out.println("##############################");
                });
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
