package com.poc.consumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.consumer.entity.TenantDataEntity;
import com.poc.consumer.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class TenantDataListener {

    private static final Logger logger = LoggerFactory.getLogger(TenantDataListener.class);

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "tenant.data.queue")  // Match your existing queue name
    public void processMessage(Map<String, Object> message) {
        try {
            String messageId = (String) message.get("message_id");
            String tenantId = (String) message.get("tenant_id");
            Map<String, Object> data = (Map<String, Object>) message.get("data");

            logger.info("Received message at 2025-07-05 04:11:31 UTC - Tenant: {}, MessageId: {}",
                    tenantId, messageId);

            // Create entity using your constructor
            TenantDataEntity tenantDataEntity = new TenantDataEntity(
                    tenantId,
                    data,
                    messageId,
                    LocalDateTime.now()
            );

            // Save to database using your repository
            dataRepository.save(tenantDataEntity);

            logger.info("Processed and saved by Krup-boop - Tenant: {}, MessageId: {}, Time: 2025-07-05 04:11:31",
                    tenantId, messageId);

        } catch (Exception e) {
            logger.error("Error processing message from Krup-boop at 2025-07-05 04:11:31: {}", e.getMessage(), e);
        }
    }
}