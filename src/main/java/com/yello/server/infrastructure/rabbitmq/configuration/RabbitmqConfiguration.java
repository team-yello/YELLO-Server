package com.yello.server.infrastructure.rabbitmq.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfiguration {

    private static final String ExchangeName = "notification-exchange";
    private static final String VoteAvailableNotificationQueueName = "vote-available-notification-queue";

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Bean
    Queue VotAvailableNotificationQueue() {
        return new Queue(VoteAvailableNotificationQueueName, false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(ExchangeName);
    }

    @Bean
    Binding keywordQueueBinding(Queue voteAvailableNotificationQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(voteAvailableNotificationQueue)
            .to(topicExchange)
            .with("push.available");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory factory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(factory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory factory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }
}
