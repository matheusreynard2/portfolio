// infrastructure/config/AsyncConfig.java
package com.prodify.produto_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class AsyncConfig {

    @Bean
    public ExecutorService dbPool() {
        return new ThreadPoolExecutor(
                4,                       // núcleos
                16,                      // máximo
                60L, TimeUnit.SECONDS,   // ociosidade
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
