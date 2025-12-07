package com.example.onlinetest.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Domain.Dto.OrderDetailDto;
import com.example.onlinetest.Domain.Dto.OrderSummaryDto;
import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Order;
import com.example.onlinetest.Repo.OrderRepo;
import com.example.onlinetest.Repo.OrderStatus;

@Service
public class OrderService implements IOrderService {

    private final OrderRepo orderRepo;

    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public List<OrderSummaryDto> list(String username) {
        try {
            return Mapper.toOrderSummaryDtoList(orderRepo.findByUserUsername(username));
        } catch (Exception e) {
            throw new OrderException("Failed to list orders for user: " + username, e);
        }
    }

    @Override
    public OrderDetailDto get(String username, UUID orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order == null || order.getUser() == null || !username.equals(order.getUser().getUsername())) {
                return null;
            }
            return Mapper.toOrderDetailDto(order);
        } catch (Exception e) {
            throw new OrderException("Failed to fetch order details: " + orderId, e);
        }
    }

    @Override
    @Transactional
    public boolean cancel(String username, UUID orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order == null || order.getUser() == null || !username.equals(order.getUser().getUsername())) {
                return false;
            }
            if (order.getStatus() == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepo.save(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new OrderException("Failed to cancel order: " + orderId, e);
        }
    }

    @Override
    @Transactional
    public boolean requestReturn(String username, UUID orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order == null || order.getUser() == null || !username.equals(order.getUser().getUsername())) {
                return false;
            }
            if (order.getStatus() == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.RETURN_REQUESTED);
                orderRepo.save(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new OrderException("Failed to request return for order: " + orderId, e);
        }
    }
}
