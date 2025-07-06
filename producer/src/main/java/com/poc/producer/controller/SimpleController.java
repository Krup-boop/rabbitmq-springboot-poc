package com.poc.producer.controller;

import com.poc.producer.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimpleController {

    @Autowired
    private MessageService messageService;


    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "producer-service");
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("user", "Krup-boop");
        status.put("port", 8084);
        status.put("message", "Producer with RabbitMQ integration");
        status.put("rabbitmq_enabled", true);
        return status;
    }
}