package com.poc.consumer.service;

import com.poc.consumer.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@EnableScheduling
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private MessageConsumerService messageConsumerService;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void logPerformanceMetrics() {
        try {
            long totalProcessed = messageConsumerService.getProcessedMessageCount();
            long totalErrors = messageConsumerService.getErrorCount();

            // Count messages processed in last minute
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            long recentCount = dataRepository.countProcessedSince(oneMinuteAgo);

            logger.info("Performance Metrics - Total Processed: {}, Total Errors: {}, " +
                            "Last Minute: {}, TPS (last minute): {}",
                    totalProcessed, totalErrors, recentCount, recentCount / 60.0);

        } catch (Exception e) {
            logger.error("Error collecting performance metrics: {}", e.getMessage());
        }
    }
}