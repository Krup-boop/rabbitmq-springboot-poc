package com.poc.consumer.controller;

import com.poc.consumer.service.MessageConsumerService;
import com.poc.consumer.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private MessageConsumerService messageConsumerService;

    @Autowired
    private DataRepository dataRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Get processing metrics
            long totalProcessed = messageConsumerService.getProcessedMessageCount();
            long totalErrors = messageConsumerService.getErrorCount();

            // Get recent processing rate
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            long recentCount = dataRepository.countProcessedSince(oneMinuteAgo);

            status.put("service", "consumer");
            status.put("status", "healthy");
            status.put("timestamp", LocalDateTime.now());
            status.put("metrics", Map.of(
                    "totalProcessed", totalProcessed,
                    "totalErrors", totalErrors,
                    "lastMinuteCount", recentCount,
                    "approximateTPS", recentCount / 60.0
            ));

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            status.put("service", "consumer");
            status.put("status", "unhealthy");
            status.put("error", e.getMessage());
            status.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(500).body(status);
        }
    }

    @GetMapping("/metrics/reset")
    public ResponseEntity<Map<String, String>> resetMetrics() {
        messageConsumerService.resetCounters();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Metrics reset successfully");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}