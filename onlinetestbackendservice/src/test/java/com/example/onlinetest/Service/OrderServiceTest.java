package com.example.onlinetest.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Order;
import com.example.onlinetest.Repo.OrderItem;
import com.example.onlinetest.Repo.OrderRepo;
import com.example.onlinetest.Repo.OrderStatus;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;
import com.example.onlinetest.Repo.User;

class OrderServiceTest {

    @Test
    void list_returnsMappedDtos() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        User u = new User();
        u.setUsername("alice");
        Order o = new Order();
        o.setUser(u);
        o.setStatus(OrderStatus.PLACED);

        when(orderRepo.findByUserUsernameIgnoreCase("alice")).thenReturn(List.of(o));
        var dtos = svc.list("alice");
        assertEquals(1, dtos.size());
    }

    @Test
    void list_repoThrows_wrapsInOrderException() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        when(orderRepo.findByUserUsernameIgnoreCase("alice")).thenThrow(new RuntimeException("db"));

        assertThrows(OrderException.class, () -> svc.list("alice"));
    }

    @Test
    void get_notFound_returnsNull() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findByIdAndUserUsernameIgnoreCase(orderId, "alice")).thenReturn(Optional.empty());

        assertNull(svc.get("alice", orderId));
    }

    @Test
    void get_repoThrows_wrapsInOrderException() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findByIdAndUserUsernameIgnoreCase(orderId, "alice")).thenThrow(new RuntimeException("db"));

        assertThrows(OrderException.class, () -> svc.get("alice", orderId));
    }

    @Test
    void cancel_onlyPlacedCanCancel_andRestocksItems() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        User u = new User();
        u.setUsername("alice");

        Product p = new Product();
        p.id = UUID.randomUUID();
        p.name = "P";
        p.price = BigDecimal.TEN;
        p.quantity = 5;
        p.reservedQuantity = 2;

        OrderItem it = new OrderItem();
        it.setProduct(p);
        it.setQuantity(3);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(u);
        order.setStatus(OrderStatus.PLACED);
        order.setItems(new ArrayList<>(List.of(it)));

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        assertTrue(svc.cancel("alice", orderId));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8, p.getQuantity());
        // cancel does not change reservedQuantity
        assertEquals(2, p.getReservedQuantity());
        verify(productRepo).save(argThat(saved -> saved.getQuantity() == 8));
        verify(orderRepo).save(order);

        order.setStatus(OrderStatus.CANCELLED);
        assertFalse(svc.cancel("alice", orderId));
    }

    @Test
    void cancel_wrongUserOrMissingOrder_returnsFalse() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());
        assertFalse(svc.cancel("alice", orderId));

        User u = new User();
        u.setUsername("bob");
        Order order = new Order();
        order.setId(orderId);
        order.setUser(u);
        order.setStatus(OrderStatus.PLACED);
        order.setItems(new ArrayList<>());
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        assertFalse(svc.cancel("alice", orderId));
    }

    @Test
    void cancel_repoThrows_wrapsInOrderException() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findById(orderId)).thenThrow(new RuntimeException("db"));

        assertThrows(OrderException.class, () -> svc.cancel("alice", orderId));
    }

    @Test
    void requestReturn_onlyPlacedCanRequestReturn() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        User u = new User();
        u.setUsername("alice");

        Order order = new Order();
        order.setId(orderId);
        order.setUser(u);
        order.setStatus(OrderStatus.PLACED);
        order.setItems(new ArrayList<>());

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        assertTrue(svc.requestReturn("alice", orderId));
        assertEquals(OrderStatus.RETURN_REQUESTED, order.getStatus());
        verify(orderRepo).save(order);

        order.setStatus(OrderStatus.CANCELLED);
        assertFalse(svc.requestReturn("alice", orderId));
    }

    @Test
    void requestReturn_wrongUserOrMissingOrder_returnsFalse() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());
        assertFalse(svc.requestReturn("alice", orderId));

        User u = new User();
        u.setUsername("bob");
        Order order = new Order();
        order.setId(orderId);
        order.setUser(u);
        order.setStatus(OrderStatus.PLACED);
        order.setItems(new ArrayList<>());
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        assertFalse(svc.requestReturn("alice", orderId));
    }

    @Test
    void requestReturn_repoThrows_wrapsInOrderException() {
        OrderRepo orderRepo = mock(OrderRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        OrderService svc = new OrderService(orderRepo, productRepo);

        UUID orderId = UUID.randomUUID();
        when(orderRepo.findById(orderId)).thenThrow(new RuntimeException("db"));

        assertThrows(OrderException.class, () -> svc.requestReturn("alice", orderId));
    }
}
