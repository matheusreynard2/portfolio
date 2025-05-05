package com.apiestudar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
    	// Usa RabbitMQ como broker
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost") // Host do RabbitMQ
                .setRelayPort(61613)       // Porta STOMP do RabbitMQ
                .setClientLogin("guest")  // Usuário do RabbitMQ
                .setClientPasscode("guest"); // Senha do RabbitMQ

        // Define o prefixo para mensagens enviadas pelo cliente
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins(
                "http://localhost:3000", 
                "https://www.sistemaprodify.com", 
                "http://sistemaprodify.com"
            ) // Define as origens permitidas explicitamente
            .withSockJS();
    }
}