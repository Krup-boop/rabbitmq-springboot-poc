package com.poc.consumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class TenantDataDto {

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("data")
    private Map<String, Object> data;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("message_id")
    private String messageId;

    // Constructors
    public TenantDataDto() {}

    public TenantDataDto(String tenantId, Map<String, Object> data, LocalDateTime timestamp, String messageId) {
        this.tenantId = tenantId;
        this.data = data;
        this.timestamp = timestamp;
        this.messageId = messageId;
    }

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "TenantDataDto{" +
                "tenantId='" + tenantId + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}