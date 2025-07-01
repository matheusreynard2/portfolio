package com.apiestudar.api_prodify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doReturn;

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
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
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
import com.apiestudar.api_prodify.application.usecase.produto.AtualizarProdutoUseCase;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Mock
    private AtualizarProdutoUseCase atualizarProdutoUseCase;

    private AdicionarProdutoUseCase adicionarProdutoUseCase;
    private ProdutoController produtoController;
    private ExecutorService executorService;
    private PesquisasSearchBarUseCase pesquisasSearchBarUseCase;

    @BeforeEach
    void setUp() throws IOException {
        executorService = Executors.newFixedThreadPool(10);
        produtoController = new ProdutoController();
        // Instanciar mocks se necessário
        if (pesquisasSearchBarUseCase == null) {
            pesquisasSearchBarUseCase = org.mockito.Mockito.mock(PesquisasSearchBarUseCase.class);
        }
        if (adicionarProdutoUseCase == null) {
            adicionarProdutoUseCase = org.mockito.Mockito.mock(AdicionarProdutoUseCase.class);
        }
        if (atualizarProdutoUseCase == null) {
            atualizarProdutoUseCase = org.mockito.Mockito.mock(AtualizarProdutoUseCase.class);
        }
        // Injetar mocks nos campos do controller
        ReflectionTestUtils.setField(produtoController, "adicionarProduto", adicionarProdutoUseCase);
        ReflectionTestUtils.setField(produtoController, "pesquisasUseCase", pesquisasSearchBarUseCase);
        ReflectionTestUtils.setField(produtoController, "atualizarProduto", atualizarProdutoUseCase);
        // Configurar mocks normalmente
        lenient().when(mockImagemFile.getBytes()).thenReturn("imagem-teste".getBytes());
        lenient().when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(criarProdutoDTO());
        lenient().when(modelMapper.map(any(ProdutoDTO.class), any(Class.class))).thenReturn(criarProduto());
        lenient().when(modelMapper.map(any(Produto.class), any(Class.class))).thenReturn(criarProdutoDTO());
        lenient().when(produtoRepository.salvarProduto(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setId(System.currentTimeMillis());
            return produto;
        });
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            return invocation.getArgument(0, org.springframework.transaction.support.TransactionCallback.class)
                    .doInTransaction(null);
        });
        lenient().when(atualizarProdutoUseCase.executar(any(Long.class), any(ProdutoFormDTO.class), any(MultipartFile.class)))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(criarProdutoDTO()));
        doReturn(CompletableFuture.completedFuture(List.of(criarProdutoDTO())))
            .when(pesquisasSearchBarUseCase)
            .efetuarPesquisa(any(Long.class), any(Long.class), any(String.class), any(String.class), any(Long.class));

        Mockito.doAnswer(invocation -> {
            String nomePesquisado = invocation.getArgument(2);   // 3º argumento
            ProdutoDTO dtoGerado  = dtoParaNome(nomePesquisado);
        
            return CompletableFuture.completedFuture(List.of(dtoGerado));
        }).when(pesquisasSearchBarUseCase)
            .efetuarPesquisa(Mockito.anyLong(),      // idUsuario (qualquer)
                            Mockito.any(),          // id (pode ser null)
                            Mockito.anyString(),   // nome
                            Mockito.anyString(),   // nomeFornecedor
                            Mockito.anyLong());   // valorInicial
    }

    @Test
    @DisplayName("Teste de 50 requisições simultâneas para adicionar produto via endpoint")
    void deveAdicionar50ProdutosSimultaneosComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<ProdutoDTO>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        System.out.println(" ############# INICIANDO TESTE ADICIONAR " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.currentTimeMillis();
        lenient().when(adicionarProdutoUseCase.executar(any(ProdutoFormDTO.class), any(MultipartFile.class)))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(criarProdutoDTO()));
        for (int i = 1; i <= numeroRequisicoes; i++) {
            final int idx = i - 1;
            ProdutoFormDTO produtoForm = criarProdutoForm(i);
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = produtoController.adicionarProduto(produtoForm)
                .whenComplete((response, ex) -> {
                    long fimReq = System.nanoTime();
                    long duracao = (fimReq - inicioReq);
                    temposIndividuais[idx] = duracao;
                    System.out.println("[AdicionarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE ADICIONAR #############");
        System.out.println("Tempo total: " + tempoTotal + " ms");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ms");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = futures.get(i);
            ResponseEntity<ProdutoDTO> response = future.get();
            assertNotNull(response, "Response não deve ser nulo");
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertNotNull(response.getBody().getId(), "ID do produto deve estar presente");
        }
        assertTrue(tempoTotal < 5000, "Tempo total deve ser menor que 5 segundos");
        assertEquals(numeroRequisicoes, futures.size(), "Deve ter processado todas as requisições");
    }

    @Test
    @DisplayName("Deve processar 50 requisições simultâneas de busca por nome de produto via endpoint")
    void deveProcessar50BuscasSimultaneasPorNomeComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<List<ProdutoDTO>>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String nomeBase = "ProdutoTeste";
        Long idUsuario = 1L;
        System.out.println(" ############# INICIANDO TESTE BUSCAS " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.currentTimeMillis();
        Mockito.doAnswer(invocation -> {
            String nomePesquisado = invocation.getArgument(2);
            ProdutoDTO dtoGerado = dtoParaNome(nomePesquisado);
            return CompletableFuture.completedFuture(List.of(dtoGerado));
        }).when(pesquisasSearchBarUseCase)
            .efetuarPesquisa(Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            String nomePesquisa = nomeBase + i;
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<List<ProdutoDTO>>> future = produtoController.efetuarPesquisa(idUsuario, null, nomePesquisa, null, null)
                .whenComplete((response, ex) -> {
                    long fimReq = System.nanoTime();
                    long duracao = (fimReq - inicioReq);
                    temposIndividuais[idx] = duracao;
                    System.out.println("[BuscaProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE BUSCAS #############");
        System.out.println("Tempo total: " + tempoTotal + " ms");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ms");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (int i = 0; i < futures.size(); i++) {
            ResponseEntity<List<ProdutoDTO>> response = futures.get(i).get();
            List<ProdutoDTO> resultado = response.getBody();
            assertEquals(1, resultado.size(), "Esperava 1 item — índice " + i);
            ProdutoDTO dto = resultado.get(0);
            String esperado = nomeBase + i;
            assertEquals(esperado, dto.getNome(), "Nome retornado não confere — índice " + i);
            assertNotNull(resultado, "Resultado não deve ser nulo — índice " + i);
            assertEquals(1, resultado.size(), "Deve retornar 1 produto — índice " + i);
            assertTrue(resultado.get(0).getNome().startsWith(nomeBase), "Nome deve iniciar com " + nomeBase + " — índice " + i);
        }
        assertTrue(tempoTotal < 5000, "Tempo total deve ser menor que 5 s");
        assertEquals(numeroRequisicoes, futures.size(), "Processou todas as requisições");
    }

    @Test
    @DisplayName("Deve processar 50 requisições simultâneas de atualização de produto via endpoint")
    void deveProcessar50AtualizacoesSimultaneasComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<ProdutoDTO>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        System.out.println(" ############# INICIANDO TESTE ATUALIZACAO " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.currentTimeMillis();
        lenient().when(atualizarProdutoUseCase.executar(any(Long.class), any(ProdutoFormDTO.class), any(MultipartFile.class)))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(criarProdutoDTO()));
        for (int i = 1; i <= numeroRequisicoes; i++) {
            final int idx = i - 1;
            ProdutoFormDTO produtoForm = criarProdutoForm(i);
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = produtoController.atualizarProduto((long) i, produtoForm)
                .whenComplete((response, ex) -> {
                    long fimReq = System.nanoTime();
                    long duracao = (fimReq - inicioReq);
                    temposIndividuais[idx] = duracao;
                    System.out.println("[AtualizarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE ATUALIZAÇÃO #############");
        System.out.println("Tempo total: " + tempoTotal + " ms");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ms");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (int i = 0; i < futures.size(); i++) {
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = futures.get(i);
            ResponseEntity<ProdutoDTO> response = future.get();
            assertNotNull(response, "Response não deve ser nulo");
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertNotNull(response.getBody().getId(), "ID do produto deve estar presente");
        }
        assertTrue(tempoTotal < 5000, "Tempo total deve ser menor que 5 segundos");
        assertEquals(numeroRequisicoes, futures.size(), "Deve ter processado todas as requisições");
    }

    private ProdutoDTO dtoParaNome(String nome) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(System.nanoTime());   // id único só para diferenciar
        dto.setNome(nome);
        dto.setDescricao("DTO gerado para " + nome);
        return dto;
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