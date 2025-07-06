package com.poc.consumer.controller;

import com.poc.consumer.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatusController {

    @Autowired
    private DataRepository dataRepository;

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "consumer-service");
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("user", "Krup-boop");
        status.put("port", 8081);
        status.put("message", "Consumer with Database integration");
        status.put("total_records", dataRepository.count());
        status.put("current_time_utc", "2025-07-05 04:00:14");
        return status;
    }
}