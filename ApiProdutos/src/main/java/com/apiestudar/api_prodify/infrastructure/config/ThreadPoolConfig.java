package com.apiestudar.api_prodify.infrastructure.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * ═══════════════════════════════════════════════════════════════
 * CONFIGURAÇÃO OTIMIZADA DE THREAD POOLS
 * ═══════════════════════════════════════════════════════════════
 * - cpuPool: Otimizado para processamento CPU-intensivo
 * - ioPool: Otimizado para I/O com controle de crescimento
 * - dbPool: Específico para operações de banco
 * - Configurações baseadas em propriedades
 * - Shutdown graceful e monitoramento
 */
@Configuration
public class ThreadPoolConfig {

    // ═══ CONFIGURAÇÕES BASEADAS EM PROPRIEDADES ═══
    
    @Value("${app.threadpool.cpu.core-size:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}")
    private int cpuCorePoolSize;
    
    @Value("${app.threadpool.cpu.max-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() * 2}}")
    private int cpuMaxPoolSize;
    
    @Value("${app.threadpool.io.core-size:20}")
    private int ioCorePoolSize;
    
    @Value("${app.threadpool.io.max-size:100}")
    private int ioMaxPoolSize;
    
    @Value("${app.threadpool.db.core-size:10}")
    private int dbCorePoolSize;
    
    @Value("${app.threadpool.db.max-size:50}")
    private int dbMaxPoolSize;
    
    @Value("${app.threadpool.keep-alive-seconds:60}")
    private long keepAliveSeconds;

    // ═══════════════════════════════════════════════════════════════
    // CPU POOL - OTIMIZADO PARA PROCESSAMENTO
    // ═══════════════════════════════════════════════════════════════

    /**
     * ⚡ CPU POOL - Para operações CPU-intensivas
     * 
     * CARACTERÍSTICAS:
     * - Tamanho fixo baseado em número de cores
     * - Sem crescimento dinâmico (evita context switching)
     * - Fila limitada para evitar acúmulo
     * - Prioridade alta para processamento
     * 
     * USO: Conversões DTO, validações, cálculos, parsing JSON
     */
    @Bean(name = "cpuPool", destroyMethod = "shutdown")
    public ExecutorService cpuPool() {
        
        int cores = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            cores,                          // corePoolSize: exato número de cores
            cores,                          // maximumPoolSize: não cresce (CPU-bound)
            keepAliveSeconds,               // keepAliveTime
            TimeUnit.SECONDS,               // timeUnit
            new LinkedBlockingQueue<>(cores * 2), // workQueue: fila limitada
            createCpuThreadFactory(),       // threadFactory: prioridade alta
            new CpuRejectionHandler()       // rejectedExecutionHandler
        );
        
        // ⚙️ CONFIGURAÇÕES OTIMIZADAS
        executor.allowCoreThreadTimeOut(false); // Mantém threads core vivas
        executor.prestartAllCoreThreads();      // Pré-inicia todas as threads
        
        logPoolConfiguration("CPU", cores, cores, cores * 2);
        
        return executor;
    }

    // ═══════════════════════════════════════════════════════════════
    // I/O POOL - OTIMIZADO PARA OPERAÇÕES I/O
    // ═══════════════════════════════════════════════════════════════

    /**
     * ⚡ I/O POOL - Para operações I/O que podem bloquear
     * 
     * CARACTERÍSTICAS:
     * - Pool expansível (core -> max conforme demanda)
     * - Fila maior para absorver picos
     * - Threads podem morrer quando ociosas
     * - Otimizado para operações que bloqueiam
     * 
     * USO: HTTP calls, file operations, operações de rede
     */
    @Bean(name = "ioPool", destroyMethod = "shutdown")
    public ExecutorService ioPool() {
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            ioCorePoolSize,                 // corePoolSize: base conservadora
            ioMaxPoolSize,                  // maximumPoolSize: pode crescer
            keepAliveSeconds,               // keepAliveTime
            TimeUnit.SECONDS,               // timeUnit
            new LinkedBlockingQueue<>(ioMaxPoolSize * 2), // workQueue: fila grande
            createIoThreadFactory(),        // threadFactory: prioridade normal
            new IoRejectionHandler()        // rejectedExecutionHandler
        );
        
        // ⚙️ CONFIGURAÇÕES OTIMIZADAS
        executor.allowCoreThreadTimeOut(true);  // Permite threads core morrerem
        executor.prestartCoreThread();           // Pré-inicia apenas uma thread core
        
        logPoolConfiguration("I/O", ioCorePoolSize, ioMaxPoolSize, ioMaxPoolSize * 2);
        
        return executor;
    }

    // ═══════════════════════════════════════════════════════════════
    // DB POOL - ESPECÍFICO PARA OPERAÇÕES DE BANCO
    // ═══════════════════════════════════════════════════════════════

    /**
     * ⚡ DB POOL - Para operações de banco de dados
     * 
     * CARACTERÍSTICAS:
     * - Configuração intermediária entre CPU e I/O
     * - Tamanho controlado para não sobrecarregar o DB
     * - Timeout específico para operações de banco
     * - Fila equilibrada
     * 
     * USO: Queries, transações, operações de repository
     */
    @Bean(name = "dbPool", destroyMethod = "shutdown")
    public ExecutorService dbPool() {
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            dbCorePoolSize,                 // corePoolSize: conservador
            dbMaxPoolSize,                  // maximumPoolSize: limitado pelo DB
            keepAliveSeconds,               // keepAliveTime
            TimeUnit.SECONDS,               // timeUnit
            new LinkedBlockingQueue<>(dbMaxPoolSize), // workQueue: proporcional
            createDbThreadFactory(),        // threadFactory: prioridade normal
            new DbRejectionHandler()        // rejectedExecutionHandler
        );
        
        // ⚙️ CONFIGURAÇÕES OTIMIZADAS
        executor.allowCoreThreadTimeOut(false); // Mantém conexões estáveis
        executor.prestartCoreThread();           // Pré-inicia algumas threads
        
        logPoolConfiguration("DB", dbCorePoolSize, dbMaxPoolSize, dbMaxPoolSize);
        
        return executor;
    }

    // ═══════════════════════════════════════════════════════════════
    // POOL GERAL (COMPATIBILIDADE)
    // ═══════════════════════════════════════════════════════════════

    /**
     * 🔄 POOL GERAL - Mantém compatibilidade com código existente
     */
    @Bean(name = "executorService", destroyMethod = "shutdown")
    @Primary
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(20, 
            new ThreadFactoryBuilder()
                .setNameFormat("general-pool-%d")
                .setDaemon(false)
                .setPriority(Thread.NORM_PRIORITY)
                .build());
    }

    // ═══════════════════════════════════════════════════════════════
    // THREAD FACTORIES CUSTOMIZADAS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Thread Factory para CPU Pool - Prioridade alta
     */
    private ThreadFactory createCpuThreadFactory() {
        return new ThreadFactoryBuilder()
            .setNameFormat("cpu-pool-%d")
            .setDaemon(false)
            .setPriority(Thread.MAX_PRIORITY)      // ⚡ Prioridade máxima
            .setUncaughtExceptionHandler(this::handleCpuException)
            .build();
    }

    /**
     * Thread Factory para I/O Pool - Prioridade normal
     */
    private ThreadFactory createIoThreadFactory() {
        return new ThreadFactoryBuilder()
            .setNameFormat("io-pool-%d")
            .setDaemon(false)
            .setPriority(Thread.NORM_PRIORITY)     // ⚖️ Prioridade normal
            .setUncaughtExceptionHandler(this::handleIoException)
            .build();
    }

    /**
     * Thread Factory para DB Pool - Prioridade normal
     */
    private ThreadFactory createDbThreadFactory() {
        return new ThreadFactoryBuilder()
            .setNameFormat("db-pool-%d")
            .setDaemon(false)
            .setPriority(Thread.NORM_PRIORITY)     // ⚖️ Prioridade normal
            .setUncaughtExceptionHandler(this::handleDbException)
            .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // REJECTION HANDLERS CUSTOMIZADOS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Handler para rejeições do CPU Pool
     */
    private static class CpuRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("⚠️ CPU Pool: Tarefa rejeitada - Pool sobrecarregado!");
            // Estratégia: Executa na thread atual (caller runs)
            if (!executor.isShutdown()) {
                r.run();
            }
        }
    }

    /**
     * Handler para rejeições do I/O Pool
     */
    private static class IoRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("⚠️ I/O Pool: Tarefa rejeitada - Muitas operações simultâneas!");
            // Estratégia: Descarta a tarefa mais antiga e executa a nova
            if (!executor.isShutdown()) {
                executor.getQueue().poll();
                executor.execute(r);
            }
        }
    }

    /**
     * Handler para rejeições do DB Pool
     */
    private static class DbRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("⚠️ DB Pool: Tarefa rejeitada - Banco sobrecarregado!");
            // Estratégia: Bloqueia até haver espaço (careful with this)
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // EXCEPTION HANDLERS
    // ═══════════════════════════════════════════════════════════════

    private void handleCpuException(Thread thread, Throwable exception) {
        System.err.printf("🔥 Erro no CPU Pool [%s]: %s%n", thread.getName(), exception.getMessage());
        exception.printStackTrace();
    }

    private void handleIoException(Thread thread, Throwable exception) {
        System.err.printf("🔥 Erro no I/O Pool [%s]: %s%n", thread.getName(), exception.getMessage());
        exception.printStackTrace();
    }

    private void handleDbException(Thread thread, Throwable exception) {
        System.err.printf("🔥 Erro no DB Pool [%s]: %s%n", thread.getName(), exception.getMessage());
        exception.printStackTrace();
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITÁRIOS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Log da configuração dos pools
     */
    private void logPoolConfiguration(String poolName, int core, int max, int queueSize) {
        System.out.printf("⚙️ %s Pool configurado: core=%d, max=%d, queue=%d%n", 
            poolName, core, max, queueSize);
    }

    /**
     * Bean para monitoramento opcional
     */
    @Bean
    public ThreadPoolMonitoringService threadPoolMonitoringService() {
        return new ThreadPoolMonitoringService();
    }

    /**
     * Serviço de monitoramento dos pools
     */
    public static class ThreadPoolMonitoringService {
        
        public void logPoolStats(ThreadPoolExecutor executor, String poolName) {
            System.out.printf("📊 %s Pool Stats: active=%d, completed=%d, queue=%d%n",
                poolName,
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getQueue().size()
            );
        }
        
        public boolean isPoolHealthy(ThreadPoolExecutor executor, double maxUtilization) {
            double utilization = (double) executor.getActiveCount() / executor.getMaximumPoolSize();
            return utilization < maxUtilization;
        }
    }
}