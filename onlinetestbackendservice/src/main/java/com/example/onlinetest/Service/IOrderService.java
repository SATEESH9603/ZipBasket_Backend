package com.example.onlinetest.Service;

import java.util.List;
import java.util.UUID;

import com.example.onlinetest.Domain.Dto.OrderDetailDto;
import com.example.onlinetest.Domain.Dto.OrderSummaryDto;

public interface IOrderService {
    List<OrderSummaryDto> list(String username);
    OrderDetailDto get(String username, UUID orderId);
    boolean cancel(String username, UUID orderId);
    boolean requestReturn(String username, UUID orderId);
}
