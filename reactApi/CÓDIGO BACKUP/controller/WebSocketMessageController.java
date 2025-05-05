// WebSocketMessageController.java
package com.apiestudar.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.apiestudar.entity.ChatMessage;
import com.apiestudar.rabbitmq.RabbitMQService;
import com.apiestudar.service.ChatMessageService;

@Controller
public class WebSocketMessageController {

    private final RabbitMQService rabbitMQService;   
    private final ChatMessageService chatMessageService;

    public WebSocketMessageController(RabbitMQService rabbitMQService, ChatMessageService chatMessageService) {
        this.rabbitMQService = rabbitMQService;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(String message) {
    	 // 1. Persistir no banco
        ChatMessage msg = new ChatMessage();
        msg.setMessage(message);
        msg.setTimeStamp(LocalDateTime.now());
        chatMessageService.salvarMensagem(msg);

        // 2. Enviar para o RabbitMQ
        rabbitMQService.sendMessage(message);
    }
} 