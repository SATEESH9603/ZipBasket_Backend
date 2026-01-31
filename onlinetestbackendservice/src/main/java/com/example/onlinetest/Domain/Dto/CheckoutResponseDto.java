package com.example.onlinetest.Domain.Dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckoutResponseDto {
    private boolean success;
    private String message;
    private UUID orderId;
    private BigDecimal total;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
