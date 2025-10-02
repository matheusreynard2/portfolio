package com.prodify.produto_service.application.usecase.produto;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.shared.exception.RegistroNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AtualizarProdutoUseCase {

    private final ProdutoRepository repo;
    private final ModelMapper mapper = new ModelMapper();
    private final ObjectMapper json = new ObjectMapper();

    public AtualizarProdutoUseCase(ProdutoRepository repo) {
        this.repo = repo;
    }

    public ProdutoDTO executar(long id, String produtoJson, MultipartFile imagem) throws IOException {
        long t0 = System.nanoTime();

        // Parse JSON
        ProdutoDTO dto = json.readValue(produtoJson, ProdutoDTO.class);
        Produto prodParaAtualizar = mapper.map(dto, Produto.class);

        // Carrega existente
        Produto prodExistente = repo.findByIdJoinFetch(id)
                .orElseThrow(RegistroNaoEncontradoException::new);

        // Aplica imagem se enviada
        if (imagem != null && !imagem.isEmpty()) {
            prodExistente.setImagem(imagem.getBytes());
        }

        // Copia demais campos
        copiarCampos(prodParaAtualizar, prodExistente);
        repo.salvarProduto(prodExistente);

        // Releitura
        Produto prodAtualizado = repo.findByIdJoinFetch(id)
                .orElseThrow(() -> new IllegalStateException("Produto não relido após atualização"));

        ProdutoDTO prodAtualizadoDTO = mapper.map(prodAtualizado, ProdutoDTO.class);

        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### ATUALIZAR PRODUTO %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");
        return prodAtualizadoDTO;
    }

    private void copiarCampos(Produto novo, Produto anterior) {
        anterior.setNome(novo.getNome());
        anterior.setPromocao(novo.getPromocao());
        anterior.setValorTotalDesc(novo.getValorTotalDesc());
        anterior.setSomaTotalValores(novo.getSomaTotalValores());
        anterior.setFornecedor(novo.getFornecedor());
        anterior.setValor(novo.getValor());
        anterior.setValorTotalFrete(novo.getValorTotalFrete());
        anterior.setDescricao(novo.getDescricao());
        anterior.setFrete(novo.getFrete());
        anterior.setValorInicial(novo.getValorInicial());
        anterior.setQuantia(novo.getQuantia());
        anterior.setFreteAtivo(novo.getFreteAtivo());
        anterior.setValorDesconto(novo.getValorDesconto());
    }
}
