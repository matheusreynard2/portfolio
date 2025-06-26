package com.apiestudar.api_prodify.infrastructure.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(8, new ThreadFactoryBuilder()
                        .setNameFormat("thread-pool-%d")
                        .build()
        );
    }
}