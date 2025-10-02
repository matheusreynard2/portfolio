package com.prodify.produto_service.application.usecase.produto;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.prodify.produto_service.domain.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdicionarProdutoUseCase {

    private final ProdutoRepository repo;
    private final ModelMapper mapper = new ModelMapper();
    private final ObjectMapper json = new ObjectMapper();

    public AdicionarProdutoUseCase(ProdutoRepository repo) {
        this.repo = repo;
    }

    public ProdutoDTO executar(
            String produtoJson, MultipartFile imagem) throws IOException {

        long t0 = System.nanoTime();
        Helper.verificarNull(produtoJson);
        ProdutoDTO dto = json.readValue(produtoJson, ProdutoDTO.class);
        Produto produto = mapper.map(dto, Produto.class); 

        Helper.verificarNull(imagem);
        byte[] bytes = imagem.getBytes();
        produto.setImagem(bytes);

        Produto salvo = repo.salvarProduto(produto); 
        Produto salvoCarregado = repo.findByIdJoinFetch(salvo.getId())
                .orElseThrow(() -> new IllegalStateException("Produto não relido"));
        ProdutoDTO produtoDTO = mapper.map(salvoCarregado, ProdutoDTO.class);

        /* ───────────── Métrica ───────────── */
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### CADASTRAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        return produtoDTO;
    }
}
