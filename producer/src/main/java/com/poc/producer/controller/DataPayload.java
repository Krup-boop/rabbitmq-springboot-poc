package com.poc.producer.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPayload {
    @JsonProperty("tenant_id")
    private String tenantId;
    private Object data;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
