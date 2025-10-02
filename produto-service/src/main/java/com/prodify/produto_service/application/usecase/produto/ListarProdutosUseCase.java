package com.prodify.produto_service.application.usecase.produto;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prodify.produto_service.domain.model.Produto;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.adapter.in.web.dto.PaginatedResponseDTO;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;

@Service
public class ListarProdutosUseCase {

    private final ProdutoRepository repo;
    private final ModelMapper mapper = new ModelMapper();

    public ListarProdutosUseCase(ProdutoRepository repo) {
        this.repo = repo;
    }

    public PaginatedResponseDTO<ProdutoDTO> executar(Pageable pageable, Long idUsuario) {
        long t0 = System.nanoTime();

        // 1) Busca paginada original
        Page<Produto> page = repo.listarProdutosByIdUsuario(pageable, idUsuario);
        List<Long> ids = page.getContent().stream().map(Produto::getId).toList();

        // 2) Recarrega com JOIN FETCH
        List<Produto> produtosComJoin = ids.isEmpty() ? List.of() : repo.findAllJoinFetchByIds(ids);

        // 3) Mapeia sequencialmente para DTO
        List<ProdutoDTO> destino = produtosComJoin.stream()
                .map(p -> mapper.map(p, ProdutoDTO.class))
                .toList();

        // Métrica
        long ns = System.nanoTime() - t0;
        System.out.println("##############################");
        System.out.printf("### LISTAR PRODUTOS %d ns ( %d ms)%n", ns, ns / 1_000_000);
        System.out.println("##############################");

        // 4) Retorna DTO paginado customizado
        return new PaginatedResponseDTO<>(
                destino,
                page.getTotalElements(),
                page.getTotalPages(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }
}
