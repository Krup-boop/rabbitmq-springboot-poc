package com.poc.producer.service;

import com.poc.producer.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendMessage(String tenantId, Object data) {
        try {
            String messageId = UUID.randomUUID().toString();

            Map<String, Object> message = new HashMap<>();
            message.put("message_id", messageId);
            message.put("tenant_id", tenantId);
            message.put("data", data);
            message.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            message.put("user", "Krup-boop");

            rabbitTemplate.convertAndSend(
                    RabbitConfig.TENANT_DATA_EXCHANGE,
                    RabbitConfig.TENANT_DATA_ROUTING_KEY,
                    message
            );

            logger.info("Message sent to RabbitMQ via exchange - Tenant: {}, MessageId: {}", tenantId, messageId);

            return messageId;

        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ for tenant: {} - {}", tenantId, e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }
}