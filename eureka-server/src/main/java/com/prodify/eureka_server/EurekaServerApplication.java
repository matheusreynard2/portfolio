package com.prodify.eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}

// ORDEM DE INICIALIZAÇÃO
// 1. Eureka Server (8761)
// 2. Microsserviços (ApiProdutos + produto-service)
// 3. API Gateway (8080)
// run4. Frontend Angular (4200)

// http://localhost:8761
// mvn spring-boot: