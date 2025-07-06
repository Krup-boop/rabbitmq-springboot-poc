package com.poc.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TENANT_DATA_QUEUE = "tenant.data.queue";
    public static final String TENANT_DATA_EXCHANGE = "tenant.data.exchange";
    public static final String TENANT_DATA_ROUTING_KEY = "tenant.data.routing.key";
    public static final String TENANT_DATA_DLQ = "tenant.data.dlq";
    public static final String TENANT_DATA_DLX = "tenant.data.dlx";

    @Bean
    public Queue tenantDataQueue() {
        return QueueBuilder.durable(TENANT_DATA_QUEUE)
                .withArgument("x-dead-letter-exchange", TENANT_DATA_DLX)
                .withArgument("x-dead-letter-routing-key", "dlq")
                .build();
    }

    @Bean
    public Queue tenantDataDLQ() {
        return QueueBuilder.durable(TENANT_DATA_DLQ).build();
    }

    @Bean
    public TopicExchange tenantDataExchange() {
        return new TopicExchange(TENANT_DATA_EXCHANGE);
    }

    @Bean
    public TopicExchange tenantDataDLX() {
        return new TopicExchange(TENANT_DATA_DLX);
    }

    @Bean
    public Binding tenantDataBinding() {
        return BindingBuilder
                .bind(tenantDataQueue())
                .to(tenantDataExchange())
                .with(TENANT_DATA_ROUTING_KEY);
    }

    @Bean
    public Binding tenantDataDLQBinding() {
        return BindingBuilder
                .bind(tenantDataDLQ())
                .to(tenantDataDLX())
                .with("dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}