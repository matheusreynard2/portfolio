package com.prodify.produto_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodify.produto_service.adapter.in.web.dto.ProdutoDTO;
import com.prodify.produto_service.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
 * usando MockMvc para testar o controller real com métricas completas em MS e NS.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class TesteConcorrenciaUnificado {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void testeConcorrencia50RequisicoesSimultaneas() throws Exception {
        System.out.println("==========================================");
        System.out.println("TESTE DE CONCORRÊNCIA - 50 PRODUTOS");
        System.out.println("==========================================");
        System.out.println("Configurações:");
        System.out.printf("   • Produtos: %d%n", NUMERO_PRODUTOS);
        System.out.printf("   • Threads: %d%n", NUMERO_THREADS);
        System.out.println("   • Método: Requisições HTTP via MockMvc");
        System.out.println("   • Controller: Real Spring Boot Controller");
        System.out.println();

        ExecutorService executor = Executors.newFixedThreadPool(NUMERO_THREADS);
        List<CompletableFuture<ResultadoRequisicao>> futures = new ArrayList<>();
        
        AtomicInteger contadorRequisicoes = new AtomicInteger(0);
        AtomicLong tempoInicioGlobal = new AtomicLong();
        
        // Marcar início do teste
        long inicioTestNS = System.nanoTime();
        tempoInicioGlobal.set(inicioTestNS);

        System.out.println("Iniciando execução das requisições...");
        
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
                        ("imagem_produto_" + indice).getBytes()
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
                    
                    int numeroRequisicao = contadorRequisicoes.incrementAndGet();
                    System.out.printf("Requisição %d/%d concluída em %.2f ms%n", 
                        numeroRequisicao, NUMERO_PRODUTOS, duracaoNS / 1_000_000.0);
                    
                    return new ResultadoRequisicao(indice, duracaoNS, true, null);
                    
                } catch (Exception e) {
                    long fimRequisicao = System.nanoTime();
                    long duracaoNS = fimRequisicao - tempoInicioGlobal.get();
                    
                    int numeroRequisicao = contadorRequisicoes.incrementAndGet();
                    System.out.printf("Requisição %d/%d falhou: %s%n", 
                        numeroRequisicao, NUMERO_PRODUTOS, e.getMessage());
                    
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
        
        // Coletar resultados
        List<ResultadoRequisicao> resultados = new ArrayList<>();
        for (CompletableFuture<ResultadoRequisicao> future : futures) {
            resultados.add(future.get());
        }
        
        // Calcular estatísticas
        exibirEstatisticas(resultados, inicioTestNS, fimTestNS);
    }

    /**
     * Cria um produto de teste simples
     */
    private ProdutoDTO criarProdutoTeste(int index) {
        return ProdutoDTO.builder()
                .id(null) // Será gerado automaticamente
                .idUsuario(1L)
                .nome("Produto Concorrência HTTP " + index)
                .promocao(index % 2 == 0)
                .valor(new BigDecimal("10.00").multiply(new BigDecimal(index + 1)))
                .valorInicial(new BigDecimal("8.00").multiply(new BigDecimal(index + 1)))
                .frete(new BigDecimal("5.00"))
                .freteAtivo(true)
                .quantia((long) (index + 1))
                .descricao("Produto HTTP de teste para concorrência número " + index)
                .fornecedor(null) // Simplificado para testes
                .valorTotalDesc(BigDecimal.ZERO)
                .build();
    }

    /**
     * Exibe estatísticas completas do teste
     */
    private void exibirEstatisticas(List<ResultadoRequisicao> resultados, long inicioNS, long fimNS) {
        System.out.println("\n==========================================");
        System.out.println("RESULTADOS DO TESTE DE CONCORRÊNCIA");
        System.out.println("==========================================");
        
        long duracaoTotalNS = fimNS - inicioNS;
        double duracaoTotalMS = duracaoTotalNS / 1_000_000.0;
        double duracaoTotalS = duracaoTotalNS / 1_000_000_000.0;
        
        // Contadores
        int sucessos = (int) resultados.stream().mapToInt(r -> r.sucesso ? 1 : 0).sum();
        int falhas = NUMERO_PRODUTOS - sucessos;
        
        // Métricas de tempo total
        System.out.println("TEMPO TOTAL:");
        System.out.printf("   • Nanossegundos: %,d NS%n", duracaoTotalNS);
        System.out.printf("   • Milissegundos: %.2f MS%n", duracaoTotalMS);
        System.out.printf("   • Segundos: %.3f S%n", duracaoTotalS);
        
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
            System.out.printf("   • Tempo Médio: %.2f MS%n", tempoMedioMS);
            System.out.printf("   • Tempo Mínimo: %d MS%n", tempoMinMS);
            System.out.printf("   • Tempo Máximo: %d MS%n", tempoMaxMS);
            
            // Throughput
            double throughputPerSecond = sucessos / duracaoTotalS;
            System.out.printf("\nTHROUGHPUT: %.2f requisições/segundo%n", throughputPerSecond);
            
            // Distribuição de tempos
            System.out.println("\n DISTRIBUIÇÃO DE TEMPOS:");
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
                .forEach(r -> System.out.printf("   • Requisição %d: %s%n", r.indice, r.erro));
        }
        
        System.out.println("\nTeste de concorrência concluído!");
        System.out.println("==========================================");
    }
} 