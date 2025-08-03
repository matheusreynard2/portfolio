package com.apiestudar.api_prodify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.apiestudar.api_prodify.interfaces.dto.ProdutoDTO;
import com.apiestudar.api_prodify.interfaces.ProdutoFeignClient;
import com.apiestudar.api_prodify.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de Concorrência Unificado - 50 Requisições HTTP Simultâneas
 * 
 * Este teste simula 50 requisições HTTP simultâneas ao endpoint adicionarProduto
 * do projeto ApiProdutos usando MockMvc para testar o controller real com métricas completas em MS e NS.
 * O ProdutoFeignClient é mockado para evitar dependências externas durante o teste.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
    }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class TesteConcorrenciaRequisicaoApiProdutos {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProdutoFeignClient produtoFeignClient;

    private static final int NUMERO_PRODUTOS = 50;
    private static final int NUMERO_THREADS = 10;

    /**
     * Classe para armazenar resultados de cada requisição
     */
    private static class ResultadoRequisicao {
        final int indice;
        final long duracaoMS;
        final boolean sucesso;
        final String erro;

        ResultadoRequisicao(int indice, long duracaoNS, boolean sucesso, String erro) {
            this.indice = indice;
            this.duracaoMS = duracaoNS / 1_000_000; // Converter NS para MS
            this.sucesso = sucesso;
            this.erro = erro;
        }
    }

    @Test
    public void testeConcorrencia50RequisicoesAdicionarProdutoApiProdutos() throws Exception {
        System.out.println("==========================================");
        System.out.println("TESTE DE CONCORRÊNCIA API PRODUTOS - 50 PRODUTOS");
        System.out.println("==========================================");
        System.out.println("Configurações:");
        System.out.printf("Produtos: %d%n", NUMERO_PRODUTOS);
        System.out.printf("Threads: %d%n", NUMERO_THREADS);
        System.out.println("Método: Requisições HTTP via MockMvc");
        System.out.println("Controller: ApiProdutos ProdutoController (ResponseEntity)");
        System.out.println("Endpoint: /api/produtos/adicionarProduto");
        System.out.println("Feign Client: Mockado (sem dependências externas)");
        System.out.println();

        ExecutorService executor = Executors.newFixedThreadPool(NUMERO_THREADS);
        List<CompletableFuture<ResultadoRequisicao>> futures = new ArrayList<>();
        
        AtomicInteger contadorRequisicoes = new AtomicInteger(0);
        AtomicLong tempoInicioGlobal = new AtomicLong();
        
        // Configurar mock do Feign Client para retornar sucesso
        ProdutoDTO produtoMockResponse = ProdutoDTO.builder()
                .id(999L)
                .idUsuario(1L)
                .nome("Produto Mock Response")
                .promocao(false)
                .valor(new BigDecimal("50.00"))
                .valorInicial(new BigDecimal("45.00"))
                .frete(new BigDecimal("10.00"))
                .freteAtivo(true)
                .quantia(1L)
                .descricao("Produto de resposta mockada")
                .valorTotalDesc(BigDecimal.ZERO)
                .valorDesconto(BigDecimal.ZERO)
                .build();
        
        when(produtoFeignClient.adicionarProduto(anyString(), any(MultipartFile.class)))
                .thenReturn(new ResponseEntity<>(produtoMockResponse, HttpStatus.CREATED));

        // Marcar início do teste
        long inicioTestNS = System.nanoTime();
        tempoInicioGlobal.set(inicioTestNS);

        System.out.println("Iniciando execução das requisições...");
        System.out.println("==========================================");
        
        // Criar 50 requisições simultâneas
        for (int i = 0; i < NUMERO_PRODUTOS; i++) {
            final int indice = i;
            
            CompletableFuture<ResultadoRequisicao> future = CompletableFuture.supplyAsync(() -> {
                try {
                    long inicioRequisicao = System.nanoTime();
                    
                    // Criar produto para teste
                    ProdutoDTO produto = criarProdutoTeste(indice);
                    String produtoJson = objectMapper.writeValueAsString(produto);
                    
                    // Criar arquivo de imagem mock
                    MockMultipartFile imagemFile = new MockMultipartFile(
                        "imagemFile",
                        "produto" + indice + ".jpg",
                        "image/jpeg",
                        ("imagem_produto_apiProdutos_" + indice).getBytes()
                    );
                    
                    MockMultipartFile produtoJsonFile = new MockMultipartFile(
                        "produtoJson",
                        null,
                        MediaType.APPLICATION_JSON_VALUE,
                        produtoJson.getBytes()
                    );
                    
                    // Executar requisição HTTP real via MockMvc
                    MvcResult result = mockMvc.perform(multipart("/api/produtos/adicionarProduto")
                            .file(produtoJsonFile)
                            .file(imagemFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                            .andExpect(status().is2xxSuccessful()) // Aceita qualquer status 2xx (200, 201, etc.)
                            .andReturn();
                    
                    long fimRequisicao = System.nanoTime();
                    long duracaoNS = fimRequisicao - inicioRequisicao;
                    double duracaoMS = duracaoNS / 1_000_000.0;
                    
                    int numeroRequisicao = contadorRequisicoes.incrementAndGet();
                    
                    // Log detalhado de sucesso com tempo em MS e NS
                    synchronized (System.out) {
                        System.out.printf("SUCESSO - Requisição #%d (Produto: %s) executada com SUCESSO!%n", 
                            numeroRequisicao, produto.getNome());
                        System.out.printf("Tempo de execução: %.2f MS (%,d NS)%n", duracaoMS, duracaoNS);
                        System.out.printf("Progresso: %d/%d requisições concluídas%n", numeroRequisicao, NUMERO_PRODUTOS);
                        System.out.println("Partindo para a próxima requisição...");
                        System.out.println("------------------------------------------");
                    }
                    
                    return new ResultadoRequisicao(indice, duracaoNS, true, null);
                    
                } catch (Exception e) {
                    long fimRequisicao = System.nanoTime();
                    long duracaoNS = fimRequisicao - tempoInicioGlobal.get();
                    double duracaoMS = duracaoNS / 1_000_000.0;
                    
                    int numeroRequisicao = contadorRequisicoes.incrementAndGet();
                    
                    // Log detalhado de falha com tempo em MS e NS
                    synchronized (System.out) {
                        System.out.printf("FALHA - Requisição #%d FALHOU!%n", numeroRequisicao);
                        System.out.printf("Tempo até falha: %.2f MS (%,d NS)%n", duracaoMS, duracaoNS);
                        System.out.printf("Erro: %s%n", e.getMessage());
                        System.out.printf("Progresso: %d/%d requisições processadas%n", numeroRequisicao, NUMERO_PRODUTOS);
                        System.out.println("Partindo para a próxima requisição...");
                        System.out.println("------------------------------------------");
                    }
                    
                    return new ResultadoRequisicao(indice, duracaoNS, false, e.getMessage());
                }
            }, executor);
            
            futures.add(future);
        }

        // Aguardar conclusão de todas as requisições
        CompletableFuture<Void> todasRequisicoes = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        todasRequisicoes.get(30, TimeUnit.SECONDS); // Timeout de 30s
        long fimTestNS = System.nanoTime();
        
        executor.shutdown();
        
        System.out.println("TODAS AS 50 REQUISIÇÕES FINALIZADAS!");
        System.out.println("==========================================");
        
        // Coletar resultados
        List<ResultadoRequisicao> resultados = new ArrayList<>();
        for (CompletableFuture<ResultadoRequisicao> future : futures) {
            resultados.add(future.get());
        }
        
        // Calcular estatísticas
        exibirEstatisticas(resultados, inicioTestNS, fimTestNS);
    }

    /**
     * Cria um produto de teste simples para o projeto ApiProdutos
     */
    private ProdutoDTO criarProdutoTeste(int index) {
        return ProdutoDTO.builder()
                .id(null) // Será gerado automaticamente
                .idUsuario(1L)
                .nome("Produto API Concorrência " + index)
                .promocao(index % 2 == 0)
                .valor(new BigDecimal("15.00").multiply(new BigDecimal(index + 1)))
                .valorInicial(new BigDecimal("12.00").multiply(new BigDecimal(index + 1)))
                .frete(new BigDecimal("7.50"))
                .freteAtivo(true)
                .quantia((long) (index + 1))
                .descricao("Produto de teste para concorrência API Produtos número " + index)
                .fornecedor(null) // Simplificado para testes
                .valorTotalDesc(BigDecimal.ZERO)
                .valorDesconto(BigDecimal.ZERO)
                .build();
    }

    /**
     * Exibe estatísticas completas do teste
     */
    private void exibirEstatisticas(List<ResultadoRequisicao> resultados, long inicioNS, long fimNS) {
        System.out.println("\n==========================================");
        System.out.println("RESULTADOS DO TESTE DE CONCORRÊNCIA API PRODUTOS");
        System.out.println("==========================================");
        
        long duracaoTotalNS = fimNS - inicioNS;
        double duracaoTotalMS = duracaoTotalNS / 1_000_000.0;
        double duracaoTotalS = duracaoTotalNS / 1_000_000_000.0;
        
        // Contadores
        int sucessos = (int) resultados.stream().mapToInt(r -> r.sucesso ? 1 : 0).sum();
        int falhas = NUMERO_PRODUTOS - sucessos;
        
        // Métricas de tempo total
        System.out.println("TEMPO TOTAL:");
        System.out.printf("Nanossegundos: %,d NS%n", duracaoTotalNS);
        System.out.printf("Milissegundos: %.2f MS%n", duracaoTotalMS);
        System.out.printf("Segundos: %.3f S%n", duracaoTotalS);
        
        // Taxa de sucesso
        System.out.printf("\nSUCESSOS: %d/%d (%.1f%%)%n", sucessos, NUMERO_PRODUTOS, (sucessos * 100.0) / NUMERO_PRODUTOS);
        System.out.printf("FALHAS: %d/%d (%.1f%%)%n", falhas, NUMERO_PRODUTOS, (falhas * 100.0) / NUMERO_PRODUTOS);
        
        if (sucessos > 0) {
            List<ResultadoRequisicao> sucessosOnly = resultados.stream()
                .filter(r -> r.sucesso)
                .toList();
            
            // Estatísticas de tempo individual
            double tempoMedioMS = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).average().orElse(0);
            long tempoMinMS = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).min().orElse(0);
            long tempoMaxMS = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).max().orElse(0);
            
            System.out.println("\nTEMPO POR REQUISIÇÃO (apenas sucessos):");
            System.out.printf("Tempo Médio: %.2f MS%n", tempoMedioMS);
            System.out.printf("Tempo Mínimo: %d MS%n", tempoMinMS);
            System.out.printf("Tempo Máximo: %d MS%n", tempoMaxMS);
            
            // Throughput
            double throughputPerSecond = sucessos / duracaoTotalS;
            System.out.printf("\nTHROUGHPUT: %.2f requisições/segundo%n", throughputPerSecond);
            
            // Distribuição de tempos
            System.out.println("\nDISTRIBUIÇÃO DE TEMPOS:");
            long countRapidas = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).filter(t -> t < 100).count();
            long countMedias = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).filter(t -> t >= 100 && t < 500).count();
            long countLentas = sucessosOnly.stream().mapToLong(r -> r.duracaoMS).filter(t -> t >= 500).count();
            
            System.out.printf("Rápidas (< 100ms): %d (%.1f%%)%n", countRapidas, (countRapidas * 100.0) / sucessos);
            System.out.printf("Médias (100-500ms): %d (%.1f%%)%n", countMedias, (countMedias * 100.0) / sucessos);
            System.out.printf("Lentas (> 500ms): %d (%.1f%%)%n", countLentas, (countLentas * 100.0) / sucessos);
        }
        
        // Mostrar erros se houver
        if (falhas > 0) {
            System.out.println("\nERROS ENCONTRADOS:");
            resultados.stream()
                .filter(r -> !r.sucesso)
                .forEach(r -> System.out.printf("Requisição %d: %s%n", r.indice, r.erro));
        }
        
        System.out.println("\nTeste de concorrência API Produtos concluído!");
        System.out.println("==========================================");
    }
} 