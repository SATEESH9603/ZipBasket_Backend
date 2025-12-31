package com.example.onlinetest.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Domain.Dto.OrderDetailDto;
import com.example.onlinetest.Domain.Dto.OrderSummaryDto;
import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Order;
import com.example.onlinetest.Repo.OrderItem;
import com.example.onlinetest.Repo.OrderRepo;
import com.example.onlinetest.Repo.OrderStatus;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;

@Service
public class OrderService implements IOrderService {

    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    public OrderService(OrderRepo orderRepo, ProductRepo productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<OrderSummaryDto> list(String username) {
        try {
            List<Order> orders = orderRepo.findByUserUsernameIgnoreCase(username);
            return Mapper.toOrderSummaryDtoList(orders);
        } catch (Exception e) {
            throw new OrderException("Failed to list orders for user: " + username, e);
        }
    }

    @Override
    public OrderDetailDto get(String username, UUID orderId) {
        try {
            Order order = orderRepo.findByIdAndUserUsernameIgnoreCase(orderId, username).orElse(null);
            if (order == null) {
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
                // Restock each product from the order items
                for (OrderItem it : order.getItems()) {
                    Product p = it.getProduct();
                    if (p != null) {
                        int qty = Math.max(0, p.getQuantity());
                        int add = Math.max(0, it.getQuantity());
                        p.setQuantity(qty + add);
                        // Do not change reservedQuantity on cancel of a placed order
                        productRepo.save(p);
                    }
                }
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

    @Override
    public List<OrderSummaryDto> listBySeller(UUID sellerId) {
        try {
            List<Order> all = orderRepo.findAll();
            List<Order> filtered = all.stream().filter(o -> {
                for (OrderItem it : o.getItems()) {
                    if (it.getProduct() != null && it.getProduct().getSeller() != null && sellerId.equals(it.getProduct().getSeller().getId())) {
                        return true;
                    }
                }
                return false;
            }).sorted(Comparator.comparing(Order::getCreatedAt).reversed()).collect(Collectors.toList());
            return Mapper.toOrderSummaryDtoList(filtered);
        } catch (Exception e) {
            throw new OrderException("Failed to list orders for seller: " + sellerId, e);
        }
    }

    @Override
    public List<OrderDetailDto> listDetailsBySeller(UUID sellerId) {
        try {
            List<Order> all = orderRepo.findAll();
            List<Order> filtered = all.stream().filter(o -> {
                for (OrderItem it : o.getItems()) {
                    if (it.getProduct() != null && it.getProduct().getSeller() != null && sellerId.equals(it.getProduct().getSeller().getId())) {
                        return true;
                    }
                }
                return false;
            }).sorted(Comparator.comparing(Order::getCreatedAt).reversed()).collect(Collectors.toList());
            return filtered.stream().map(Mapper::toOrderDetailDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new OrderException("Failed to list detailed orders for seller: " + sellerId, e);
        }
    }

    @Override
    public List<OrderSummaryDto> listBySellerPaged(UUID sellerId, int page, int size) {
        try {
            PageRequest pr = PageRequest.of(Math.max(0, page - 1), Math.max(1, size));
            Page<Order> pg = orderRepo.findAll(pr);
            List<Order> filtered = pg.getContent().stream().filter(o -> {
                for (OrderItem it : o.getItems()) {
                    if (it.getProduct() != null && it.getProduct().getSeller() != null && sellerId.equals(it.getProduct().getSeller().getId())) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            return Mapper.toOrderSummaryDtoList(filtered);
        } catch (Exception e) {
            throw new OrderException("Failed to list paged orders for seller: " + sellerId, e);
        }
    }
}
