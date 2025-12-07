package com.example.onlinetest.Domain.Dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderSummaryDto {
    private UUID id;
    private String status;
    private BigDecimal total;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
