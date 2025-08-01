/*package com.apiestudar.api_prodify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import com.apiestudar.api_prodify.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import com.apiestudar.api_prodify.application.usecase.produto.*;
import com.apiestudar.api_prodify.domain.model.Produto;
import com.apiestudar.api_prodify.domain.repository.*;
import com.apiestudar.api_prodify.interfaces.dto.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TesteConcorrencia {

    /* mocks que o Spring deve injetar no contexto 
    // Remover os @MockBean dos UseCases
    // @MockBean private AdicionarProdutoUseCase   adicionarUseCase;
    // @MockBean private AtualizarProdutoUseCase   atualizarUseCase;
    // @MockBean private PesquisasSearchBarUseCase pesquisasUseCase;
    // @MockBean private DeletarProdutoUseCase deletarUseCase;

    // Instanciar manualmente os UseCases
    private AdicionarProdutoUseCase adicionarUseCase;
    private AtualizarProdutoUseCase atualizarUseCase;
    private PesquisasSearchBarUseCase pesquisasUseCase;
    private DeletarProdutoUseCase deletarUseCase;*/

    /* se precisar mockar repo/mapper, use @MockBean também 
    @MockBean private UsuarioRepository usuarioRepo;
    @MockBean private ProdutoRepository produtoRepo;
    @MockBean private ModelMapper       modelMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    /* ───────────────────────────────────────────────────────── 

    @BeforeEach
    void configurarStubs() {

        Produto prod = new Produto();
        prod.setId(1L);
        prod.setNome("Mock-Produto");
        prod.setValor(BigDecimal.TEN);
        prod.setQuantia(1L);

        Page<Produto> pPage =
        new PageImpl<>(List.of(prod),
                       PageRequest.of(0,10),
                       1L);

        ExecutorService dbPool = Executors.newFixedThreadPool(4);
        adicionarUseCase = new AdicionarProdutoUseCase(produtoRepo, dbPool);
        atualizarUseCase = new AtualizarProdutoUseCase(produtoRepo, dbPool);
        pesquisasUseCase = new PesquisasSearchBarUseCase(produtoRepo, usuarioRepo, dbPool);
        deletarUseCase = new DeletarProdutoUseCase(produtoRepo, dbPool, modelMapper);

        when(adicionarUseCase.executar(any(), any()))
            .thenAnswer(inv -> CompletableFuture.completedFuture(criarProdutoDTO(1)));
    
        when(atualizarUseCase.executar(anyLong(), any(), any()))
            .thenAnswer(inv -> CompletableFuture.completedFuture(criarProdutoDTO(2)));

        when(deletarUseCase.executar(anyLong()))
            .thenReturn(CompletableFuture.completedFuture(null));     // ❷ garante true

            when(produtoRepo.listarProdutosByIdUsuario(any(Pageable.class), eq(1L)))
            .thenReturn(pPage); 
    
            when(pesquisasUseCase.efetuarPesquisa(
                anyLong(),                       // idUsuario
                nullable(Long.class),            // idProd  (pode ser null)
                nullable(String.class),          // nomeProd (pode ser null)
                nullable(String.class),          // nomeFornecedor (pode ser null)
                nullable(Long.class)             // valorInicial (pode ser null)
        )).thenAnswer(inv -> {
            String nome = inv.getArgument(2, String.class); // pode vir null
            ProdutoDTO dto = new ProdutoDTO();
            dto.setId(ThreadLocalRandom.current().nextLong());
            dto.setNome(nome == null ? "SEM_NOME" : nome);
            return CompletableFuture.completedFuture(List.of(dto));
        });
    }

    private ProdutoFormDTO criarProdutoForm(int i) {
        String json = String.format("""
              {"nome":"Produto %d",
              "descricao":"Desc %d",
              "valorInicial":%d,
              "quantia":%d,
              "idUsuario":1,
              "idFornecedor":1,
              "promocao":false,
              "freteAtivo":true,
              "frete":10.0,
              "valorDesconto":0.0}""",
             i, i, 100+i, 5+i);
        ProdutoFormDTO f = new ProdutoFormDTO();
        f.setProdutoJson(json);
        return f;
    }

    private ProdutoDTO criarProdutoDTO(long id) {
        ProdutoDTO d = new ProdutoDTO();
        d.setId(id);
        d.setNome("Produto Teste");
        d.setDescricao("Mock");
        d.setValor(BigDecimal.TEN);
        d.setQuantia(1L);
        return d;
    }

    @Test
    void deveAdicionar50ProdutosSimultaneosComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<ProdutoDTO>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String baseUrl = "http://localhost:" + port + "/api/produtos/adicionarProduto";
        System.out.println("############# INICIANDO TESTE ADICIONAR " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            ProdutoFormDTO produtoForm = criarProdutoForm(i);
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = CompletableFuture.supplyAsync(() -> {

                MultiValueMap<String,Object> form = new LinkedMultiValueMap<>();
                form.add("produtoJson", produtoForm.getProdutoJson());
                form.add("imagemFile",
                        new ByteArrayResource("img".getBytes()) {
                            @Override public String getFilename() { return "img.jpg"; }
                        });
                HttpHeaders h = new HttpHeaders();
                h.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String,Object>> req = new HttpEntity<>(form,h);

                ResponseEntity<ProdutoDTO> resp =
                        rest.postForEntity(baseUrl, req, ProdutoDTO.class);

                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[AdicionarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Status: " + resp.getStatusCode());
                return resp;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE ADICIONAR #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<ProdutoDTO>> future : futures) {
            ResponseEntity<ProdutoDTO> response = future.get();
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertNotNull(response.getBody().getId(), "ID do produto deve estar presente");
        }
    }

    @Test
    void deveAtualizar50ProdutosSimultaneosComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<ProdutoDTO>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String baseUrl = "http://localhost:" + port + "/api/produtos/atualizarProduto";
        System.out.println("############# INICIANDO TESTE ATUALIZAR " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            ProdutoFormDTO produtoForm = criarProdutoForm(i);
            long inicioReq = System.nanoTime();
            String url = baseUrl + "/" + i;
            CompletableFuture<ResponseEntity<ProdutoDTO>> future = CompletableFuture.supplyAsync(() -> {
                MultiValueMap<String,Object> form = new LinkedMultiValueMap<>();
                form.add("produtoJson", produtoForm.getProdutoJson());
                form.add("imagemFile", new ByteArrayResource("img".getBytes()) {
                    @Override public String getFilename() { return "img.jpg"; }
                });
                HttpHeaders h = new HttpHeaders();
                h.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String,Object>> req = new HttpEntity<>(form,h);
                ResponseEntity<ProdutoDTO> resp = rest.exchange(url, HttpMethod.PUT, req, ProdutoDTO.class);
                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[AtualizarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Status: " + resp.getStatusCode());
                return resp;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE ATUALIZAR #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<ProdutoDTO>> future : futures) {
            ResponseEntity<ProdutoDTO> response = future.get();
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertNotNull(response.getBody().getId(), "ID do produto deve estar presente");
        }
    }

    @Test
    void deveBuscar50ProdutosSimultaneosPorNomeComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<List<ProdutoDTO>>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String nomeBase = "ProdutoTeste";
        Long idUsuario = 1L;
        String baseUrl = "http://localhost:" + port + "/api/produtos/efetuarPesquisa";
        System.out.println("############# INICIANDO TESTE BUSCAS " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            String nomePesquisa = nomeBase + i;
            String url = baseUrl + "?idUsuario=" + idUsuario + "&nomeProduto=" + nomePesquisa;
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<List<ProdutoDTO>>> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<List<ProdutoDTO>> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProdutoDTO>>() {}
                );
                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[BuscarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Status: " + response.getStatusCode());
                return response;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE BUSCAS #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<List<ProdutoDTO>>> future : futures) {
            ResponseEntity<List<ProdutoDTO>> response = future.get();
            if (response.getStatusCode().is2xxSuccessful()) {
                assertNotNull(response.getBody());
            }
        }
    }

    @Test
    void deveDeletar50ProdutosSimultaneosComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<Long> idsProdutos = new ArrayList<>();
        // Mockar/inserir 50 produtos válidos
        for (int i = 0; i < numeroRequisicoes; i++) {
            Produto produto = new Produto();
            produto.setId((long) (1000 + i));
            produto.setIdUsuario(1L);
            produto.setNome("Produto Teste " + i);
            produto.setPromocao(false);
            produto.setValor(BigDecimal.valueOf(10.0 + i));
            produto.setDescricao("Produto para deleção " + i);
            produto.setFrete(BigDecimal.valueOf(5.0));
            produto.setFreteAtivo(true);
            produto.setQuantia(1L);
            // Fornecedor pode ser null ou mockado, conforme contexto do projeto
            // produto.setFornecedor(...);
            produtoRepo.salvarProduto(produto);
            idsProdutos.add(produto.getId());
        }
        List<CompletableFuture<ResponseEntity<Boolean>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String baseUrl = "http://localhost:" + port + "/api/produtos/deletarProduto/";
        System.out.println("############# INICIANDO TESTE DELETAR " + numeroRequisicoes + " PRODUTOS SIMULTANEOS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            final Long idProduto = idsProdutos.get(i);
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<Boolean>> future = CompletableFuture.supplyAsync(() -> {
                String url = baseUrl + idProduto.intValue();
                ResponseEntity<Boolean> resp = rest.exchange(url, HttpMethod.DELETE, null, Boolean.class);
                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[DeletarProduto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Body: " + resp.getBody());
                System.out.println("Status: " + resp.getStatusCode());
                return resp;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE DELETAR #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<Boolean>> future : futures) {
            ResponseEntity<Boolean> response = future.get();
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertTrue(response.getBody(), "Body deve ser true (deleção bem-sucedida)");
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Status deve ser 200 OK");
        }
    }

    @Test
    void deveCalcularDesconto50RequisicoesSimultaneasComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<Double>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String baseUrl = "http://localhost:" + port + "/api/produtos/calcularDesconto/";
        System.out.println("############# INICIANDO TESTE CALCULAR DESCONTO " + numeroRequisicoes + " REQUISIÇÕES SIMULTANEAS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            double valorProduto = 100.0 + i;
            double valorDesconto = 5.0 + (i % 20); // Varia de 5 a 24
            String url = baseUrl + valorProduto + "/" + valorDesconto;
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<Double>> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<Double> resp = rest.exchange(url, HttpMethod.GET, null, Double.class);
                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[CalcularDesconto] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Status: " + resp.getStatusCode());
                return resp;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE CALCULAR DESCONTO #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<Double>> future : futures) {
            ResponseEntity<Double> response = future.get();
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Status deve ser 200 OK");
        }
    }

    @Test
    void deveListarProdutos50RequisicoesSimultaneasComSucesso() throws Exception {
        int numeroRequisicoes = 50;
        List<CompletableFuture<ResponseEntity<String>>> futures = new ArrayList<>();
        long[] temposIndividuais = new long[numeroRequisicoes];
        String baseUrl = "http://localhost:" + port + "/api/produtos/listarProdutos?page=0&size=10&idUsuario=1";
        System.out.println("############# INICIANDO TESTE LISTAR PRODUTOS " + numeroRequisicoes + " REQUISIÇÕES SIMULTANEAS #############");
        long tempoInicio = System.nanoTime();
        for (int i = 0; i < numeroRequisicoes; i++) {
            final int idx = i;
            long inicioReq = System.nanoTime();
            CompletableFuture<ResponseEntity<String>> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<String> resp = rest.exchange(baseUrl, HttpMethod.GET, null, String.class);
                long fimReq = System.nanoTime();
                long duracao = fimReq - inicioReq;
                temposIndividuais[idx] = duracao;
                System.out.println("[ListarProdutos] Requisição " + (idx + 1) + " processada em " + duracao + " ns (" + (duracao / 1_000_000.0) + " ms)");
                System.out.println("Status: " + resp.getStatusCode());
                return resp;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long tempoFim = System.nanoTime();
        long tempoTotal = tempoFim - tempoInicio;
        System.out.println("############# RESULTADOS TESTE LISTAR PRODUTOS #############");
        System.out.println("Tempo total: " + tempoTotal + " ns (" + (tempoTotal / 1_000_000.0) + " ms)");
        System.out.println("Tempo médio por requisição: " + (tempoTotal / numeroRequisicoes) + " ns (" + ((tempoTotal / numeroRequisicoes) / 1_000_000.0) + " ms)");
        System.out.println("Teste concluído com sucesso!");
        System.out.println("############# FIM DO TESTE #############");
        for (CompletableFuture<ResponseEntity<String>> future : futures) {
            ResponseEntity<String> response = future.get();
            assertNotNull(response.getBody(), "Body da response não deve ser nulo");
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Status deve ser 200 OK");
        }
    }
}
*/