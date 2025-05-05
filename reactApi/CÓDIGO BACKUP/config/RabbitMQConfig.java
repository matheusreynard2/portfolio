package com.apiestudar.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
    public static final String QUEUE_NAME = "chat.queue";
    public static final String EXCHANGE_NAME = "chat.exchange";

    @Bean
    public Queue chatQueue() {
        // Argumentos opcionais para a fila
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 600000); // Tempo de vida das mensagens em milissegundos (60 segundos)
        args.put("x-max-length", 100);   // Número máximo de mensagens retidas na fila

        // Cria a fila com durabilidade e argumentos
        return new Queue(QUEUE_NAME, true, false, false, args);
    }
    
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }
    
    @Bean
    public Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }
}

//Parâmetros do Construtor da Fila :
//"chat.queue": Nome da fila.
//true: A fila é durável (persiste após reinicialização do RabbitMQ).
//false: A fila não é exclusiva (outras conexões podem acessá-la).
//false: A fila não será excluída automaticamente quando o consumidor desconectar.
//args: Argumentos adicionais, como TTL e limite de mensagens.