package com.apiestudar.api_prodify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.apiestudar.api_prodify.application.usecase.produto.AdicionarProdutoUseCase;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.ProdutoRepository;
import com.apiestudar.api_prodify.interfaces.controller.ProdutoController;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoFormDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.apiestudar.api_prodify.domain.repository.UsuarioRepository;
import com.apiestudar.api_prodify.application.usecase.produto.PesquisasSearchBarUseCase;

@ExtendWith(MockitoExtension.class)
class TesteConcorrencia {

    @Mock
    private ProdutoRepository produtoRepository;
    
    @Mock
    private MultipartFile mockImagemFile;
    
    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private UsuarioRepository usuarioRepository;

    private AdicionarProdutoUseCase adicionarProdutoUseCase;
    private ProdutoController produtoController;
    private ExecutorService executorService;
    private PesquisasSearchBarUseCase pesquisasSearchBarUseCase;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws IOException {
        executorService = Executors.newFixedThreadPool(10);
        
        // Criar instâncias reais
        adicionarProdutoUseCase = new AdicionarProdutoUseCase(produtoRepository, executorService);
        produtoController = new ProdutoController();
        pesquisasSearchBarUseCase = new PesquisasSearchBarUseCase(produtoRepository, usuarioRepository, executorService);
        
        // Injetar mocks
        ReflectionTestUtils.setField(adicionarProdutoUseCase, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(adicionarProdutoUseCase, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(adicionarProdutoUseCase, "transactionTemplate", transactionTemplate);
        ReflectionTestUtils.setField(produtoController, "adicionarProduto", adicionarProdutoUseCase);
        
        // Configurar mocks
        lenient().when(mockImagemFile.getBytes()).thenReturn("imagem-teste".getBytes());
        
        lenient().when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(criarProdutoDTO());
        
        lenient().when(modelMapper.map(any(ProdutoDTO.class), any(Class.class))).thenReturn(criarProduto());
        
        lenient().when(modelMapper.map(any(Produto.class), any(Class.class))).thenReturn(criarProdutoDTO());
        
        lenient().when(produtoRepository.adicionarProduto(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setId(System.currentTimeMillis());
            return produto;
        });
        
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, org.springframework.transaction.support.TransactionCallback.class)
                    .doInTransaction(null);
        });
    }

    @Test
    @DisplayName("Teste de 50 requisições simultâneas para adicionar produto")
    void testeConcorrencia50RequisicoesAdicionarProduto() throws Exception {
        // Arrange
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<ProdutoDTO>>> futures = new ArrayList<>();
        
        System.out.println("Iniciando teste de concorrência com " + numeroRequisicoes + " requisições simultâneas");
        
        long tempoInicio = System.currentTimeMillis();
        
        // Act - Simular 10 usuários fazendo requisições simultaneamente
        for (int i = 1; i <= numeroRequisicoes; i++) {
            ProdutoFormDTO produtoForm = criarProdutoForm(i);
            
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = 
                produtoController.adicionarProduto(produtoForm);
            
            futures.add(future);
            
            System.out.println("Usuário " + i + " iniciou requisição");
        }
        
        // Aguardar todas as requisições
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;
        
        // Assert
        System.out.println("RESULTADOS:");
        System.out.println("Tempo total: " + tempoTotal + " ms");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ms");
        
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = futures.get(i);
            
            assertTrue(future.isDone(), "Requisição " + (i + 1) + " deve estar completa");
            assertFalse(future.isCompletedExceptionally(), "Requisição " + (i + 1) + " não deve ter erro");
            
            ResponseEntity<ProdutoDTO> response = future.get();
            assertNotNull(response, "Response não deve ser nulo");
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertNotNull(response.getBody().getId(), "ID do produto deve estar presente");
        }
        
        System.out.println("Teste concluído com sucesso! Todos os produtos foram criados simultaneamente.");
        
        // Verificar performance
        assertTrue(tempoTotal < 5000, "Tempo total deve ser menor que 5 segundos");
        assertEquals(numeroRequisicoes, futures.size(), "Deve ter processado todas as requisições");
    }

    @Test
    @DisplayName("Deve processar 50 requisições simultâneas de busca por nome de produto com sucesso")
    void deveProcessar50BuscasSimultaneasPorNomeComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<List<ProdutoDTO>>> futures = new ArrayList<>();
        String nomeBase = "ProdutoTeste";
        Long idUsuario = 1L;

        System.out.println("Iniciando teste de concorrência de busca por nome com " + numeroRequisicoes + " requisições simultâneas");
        long tempoInicio = System.currentTimeMillis();

        // Mock do repositório para retornar um produto para cada nome pesquisado
        when(produtoRepository.findByNomeAndUser(anyString(), any(Long.class))).thenAnswer(invocation -> {
            String nome = invocation.getArgument(0);
            Produto produto = new Produto();
            produto.setId((long) nome.hashCode());
            produto.setNome(nome);
            produto.setIdUsuario(idUsuario);
            return List.of(produto);
        });

        // Mock do usuário
        when(usuarioRepository.buscarUsuarioPorId(any(Long.class))).thenReturn(Optional.of(new com.apiestudar.api_prodify.domain.model.Usuario()));

        // Executor para simular concorrência
        ExecutorService executor = Executors.newFixedThreadPool(20);
        ReflectionTestUtils.setField(pesquisasSearchBarUseCase, "executorService", executor);

        for (int i = 0; i < numeroRequisicoes; i++) {
            String nomePesquisa = nomeBase + i;
            CompletableFuture<List<ProdutoDTO>> future = pesquisasSearchBarUseCase
                .efetuarPesquisa("nome", nomePesquisa, idUsuario);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;

        System.out.println("RESULTADOS BUSCA POR NOME:");
        System.out.println("Tempo total: " + tempoTotal + " ms");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ms");

        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<List<ProdutoDTO>> future = futures.get(i);
            assertTrue(future.isDone(), "Requisição " + (i + 1) + " deve estar completa");
            assertFalse(future.isCompletedExceptionally(), "Requisição " + (i + 1) + " não deve ter erro");
            List<ProdutoDTO> resultado = future.get();
            assertNotNull(resultado, "Resultado não deve ser nulo");
            assertEquals(1, resultado.size(), "Deve retornar exatamente 1 produto para a busca por nome");
            assertTrue(resultado.get(0).getNome().startsWith(nomeBase), "Nome do produto deve começar com o nome base");
        }

        System.out.println("Teste de busca por nome concluído com sucesso! Todos os produtos foram buscados simultaneamente.");
        assertTrue(tempoTotal < 5000, "Tempo total deve ser menor que 5 segundos");
        assertEquals(numeroRequisicoes, futures.size(), "Deve ter processado todas as requisições");

        executor.shutdown();
    }

    private ProdutoFormDTO criarProdutoForm(int indice) {
        String json = String.format(
            "{\"nome\":\"Produto %d\",\"descricao\":\"Descrição do produto %d\",\"valor\":%d,\"quantia\":%d,\"idUsuario\":%d,\"promocao\":false,\"freteAtivo\":true,\"frete\":10.0,\"valorInicial\":%d,\"valorDesconto\":0.0}",
            indice, indice, (100 + indice * 10), (5 + indice), indice, (100 + indice * 10)
        );
        
        ProdutoFormDTO form = new ProdutoFormDTO();
        form.setProdutoJson(json);
        form.setImagemFile(mockImagemFile);
        
        return form;
    }

    private ProdutoDTO criarProdutoDTO() {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("Produto Teste");
        dto.setDescricao("Descrição teste");
        dto.setValor(BigDecimal.valueOf(100.0));
        dto.setQuantia(10L);
        dto.setIdUsuario(1L);
        dto.setPromocao(false);
        dto.setFreteAtivo(true);
        dto.setFrete(BigDecimal.valueOf(10.0));
        dto.setValorInicial(BigDecimal.valueOf(100.0));
        dto.setValorDesconto(BigDecimal.valueOf(0.0));
        return dto;
    }

    private Produto criarProduto() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição teste");
        produto.setValor(BigDecimal.valueOf(100.0));
        produto.setQuantia(10L);
        produto.setIdUsuario(1L);
        produto.setPromocao(false);
        produto.setFreteAtivo(true);
        produto.setFrete(BigDecimal.valueOf(10.0));
        produto.setValorInicial(BigDecimal.valueOf(100.0));
        produto.setValorDesconto(BigDecimal.valueOf(0.0));
        produto.setImagem("imagem-teste".getBytes());
        return produto;
    }
} 