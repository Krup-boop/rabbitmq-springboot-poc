package com.poc.consumer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "tenant_data", indexes = {
        @Index(name = "idx_tenant_data_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_tenant_data_created_at", columnList = "created_at")
})
public class TenantDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> dataPayload;

    @Column(name = "message_id", length = 255)
    private String messageId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    // Constructors
    public TenantDataEntity() {
        this.processedAt = LocalDateTime.now();
    }

    public TenantDataEntity(String tenantId, Map<String, Object> dataPayload, String messageId, LocalDateTime createdAt) {
        this.tenantId = tenantId;
        this.dataPayload = dataPayload;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(Map<String, Object> dataPayload) {
        this.dataPayload = dataPayload;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "TenantDataEntity{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", dataPayload=" + dataPayload +
                ", messageId='" + messageId + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                '}';
    }
}