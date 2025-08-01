package com.prodify.produto_service.application.usecase.produto.usecase_config;

import com.prodify.produto_service.application.usecase.produto.AdicionarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.ListarProdutosUseCase;
import com.prodify.produto_service.application.usecase.produto.PesquisasSearchBarUseCase;
import com.prodify.produto_service.application.usecase.produto.AtualizarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.DeletarProdutoUseCase;
import com.prodify.produto_service.application.usecase.produto.CalculosSobreProdutosUseCase;
import com.prodify.produto_service.application.usecase.produto.BuscarProdutoUseCase;
import org.modelmapper.ModelMapper;
import com.prodify.produto_service.domain.repository.ProdutoRepository;
import com.prodify.produto_service.domain.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class UseCaseConfig {

    @Bean
    public ListarProdutosUseCase listarProdutosUseCaseConfig(ProdutoRepository repo, ExecutorService dbPool) {
        return new ListarProdutosUseCase(repo, dbPool);
    }

    @Bean
    public AdicionarProdutoUseCase adicionarProdutoUseCaseConfig(ProdutoRepository repo, ExecutorService dbPool) {
        return new AdicionarProdutoUseCase(repo, dbPool);
    }

    @Bean
    public PesquisasSearchBarUseCase pesquisasSearchBarUseCaseConfig(ProdutoRepository repo, UsuarioRepository usuarioRepo, ExecutorService dbPool) {
        return new PesquisasSearchBarUseCase(repo, usuarioRepo, dbPool);
    }

    @Bean
    public AtualizarProdutoUseCase AtualizarProdutoUseCaseConfig(ProdutoRepository repo, ExecutorService dbPool) {
        return new AtualizarProdutoUseCase(repo, dbPool);
    }

    @Bean
    public DeletarProdutoUseCase DeletarProdutoUseCaseConfig(ProdutoRepository repo, ExecutorService dbPool, ModelMapper mapper) {
        return new DeletarProdutoUseCase(repo, dbPool, mapper);
    }

    @Bean
    public CalculosSobreProdutosUseCase CalculosSobreProdutosUseCaseConfig(ProdutoRepository repo, ExecutorService dbPool) {
        return new CalculosSobreProdutosUseCase(repo, dbPool);
    }

    @Bean
    public BuscarProdutoUseCase BuscarProdutoUseCaseConfig(ProdutoRepository repo, UsuarioRepository usuarioRepository) {
        return new BuscarProdutoUseCase(repo, usuarioRepository);
    }
}
