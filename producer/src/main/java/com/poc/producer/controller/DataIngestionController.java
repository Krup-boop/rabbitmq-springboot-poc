package com.poc.producer.controller;

import com.poc.producer.util.JwtUtils;
import com.poc.producer.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class DataIngestionController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MessageService messageService;

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestData(@RequestBody DataPayload payload) {
        String tenantId = jwtUtils.getTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Invalid or missing JWT token");
        }
        String messageId = messageService.sendMessage(tenantId, payload.getData());
        return ResponseEntity.ok().body("Message sent with ID: " + messageId);
    }
}