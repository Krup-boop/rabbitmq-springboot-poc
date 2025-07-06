package com.poc.consumer.service;

import com.poc.consumer.dto.TenantDataDto;
import com.poc.consumer.entity.TenantDataEntity;
import com.poc.consumer.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerService.class);

    @Autowired
    private DataRepository dataRepository;

    // Counter for performance metrics
    private final AtomicLong processedMessageCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    @RabbitListener(queues = "tenant.data.queue")
    @Transactional
    public void consumeTenantData(@Payload TenantDataDto tenantData) {

        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Received message for tenant: {}, MessageId: {}",
                    tenantData.getTenantId(), tenantData.getMessageId());

            // Check for duplicate messages
            if (dataRepository.existsByMessageId(tenantData.getMessageId())) {
                logger.warn("Duplicate message detected, skipping: {}", tenantData.getMessageId());
                return;
            }

            // Create entity
            TenantDataEntity entity = new TenantDataEntity(
                    tenantData.getTenantId(),
                    tenantData.getData(),
                    tenantData.getMessageId(),
                    tenantData.getTimestamp()
            );

            // Save to database
            dataRepository.save(entity);

            // Update metrics
            long processedCount = processedMessageCount.incrementAndGet();
            long processingTime = System.currentTimeMillis() - startTime;

            logger.info("Message processed successfully - MessageId: {}, TenantId: {}, " +
                            "ProcessingTime: {}ms, TotalProcessed: {}",
                    tenantData.getMessageId(), tenantData.getTenantId(),
                    processingTime, processedCount);

        } catch (Exception e) {
            errorCount.incrementAndGet();
            logger.error("Error processing message - MessageId: {}, TenantId: {}, Error: {}",
                    tenantData.getMessageId(), tenantData.getTenantId(), e.getMessage(), e);

            // Rethrow to trigger retry mechanism
            throw new RuntimeException("Failed to process message", e);
        }
    }

    public long getProcessedMessageCount() {
        return processedMessageCount.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public void resetCounters() {
        processedMessageCount.set(0);
        errorCount.set(0);
    }
}