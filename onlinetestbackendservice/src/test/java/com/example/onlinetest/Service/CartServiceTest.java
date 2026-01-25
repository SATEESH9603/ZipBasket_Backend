package com.example.onlinetest.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.onlinetest.Domain.Dto.CartUpdateItemDto;
import com.example.onlinetest.Domain.Dto.CartUpdateRequestDto;
import com.example.onlinetest.Domain.Exceptions.CartException;
import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartItem;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;

class CartServiceTest {

    @Test
    void viewCart_existingCart_returnsResponse() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        CartService service = new CartService(cartRepo, productRepo);

        var resp = service.viewCart("alice");
        assertNotNull(resp);
        // Should not create a new cart when one exists
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void viewCart_missingCart_createsAndSavesCart() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        when(cartRepo.findByUserName("alice")).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartService service = new CartService(cartRepo, productRepo);

        var resp = service.viewCart("alice");
        assertNotNull(resp);
        verify(cartRepo).save(argThat(c -> "alice".equals(c.getUserName())));
    }

    @Test
    void viewCart_repoThrows_wrapsInCartException() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        when(cartRepo.findByUserName("alice")).thenThrow(new RuntimeException("db down"));

        CartService service = new CartService(cartRepo, productRepo);

        assertThrows(CartException.class, () -> service.viewCart("alice"));
    }

    @Test
    void updateCart_invalidProductId_throwsCartException() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId("not-a-uuid");
        item.setQuantity(1);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        assertThrows(CartException.class, () -> service.updateCart("alice", req));
    }

    @Test
    void updateCart_validProduct_savesCart() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 10;
        p.reservedQuantity = 0;
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(pid.toString());
        item.setQuantity(2);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        verify(cartRepo, atLeastOnce()).save(any(Cart.class));
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void updateCart_missingCart_createsCartAndSaves() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        when(cartRepo.findByUserName("alice")).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            if (c.getItems() == null) c.setItems(new ArrayList<>());
            return c;
        });

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 10;
        p.reservedQuantity = 0;
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(pid.toString());
        item.setQuantity(1);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        verify(cartRepo, atLeastOnce()).save(argThat(c -> "alice".equals(c.getUserName())));
    }

    @Test
    void updateCart_ignoresNonPositiveQuantity_andStillSucceeds() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(UUID.randomUUID().toString());
        item.setQuantity(0);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, cart.getItems().size());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void updateCart_productNotFound_isIgnored_andStillSucceeds() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        UUID pid = UUID.randomUUID();
        when(productRepo.findById(pid)).thenReturn(Optional.empty());

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(pid.toString());
        item.setQuantity(2);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, cart.getItems().size());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void updateCart_capsQuantityToAvailableStock() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 5;
        p.reservedQuantity = 4; // available = 1
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(pid.toString());
        item.setQuantity(10);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(5, p.getReservedQuantity());
        verify(productRepo).save(argThat(saved -> saved.getReservedQuantity() == 5));
    }

    @Test
    void updateCart_releasesPreviousReservations_beforeRecreatingItems() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        UUID oldPid = UUID.randomUUID();
        Product oldProduct = new Product();
        oldProduct.id = oldPid;
        oldProduct.name = "OLD";
        oldProduct.price = BigDecimal.ONE;
        oldProduct.quantity = 10;
        oldProduct.reservedQuantity = 7;

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());

        CartItem existing = new CartItem();
        existing.setCart(cart);
        existing.setProduct(oldProduct);
        existing.setQuantity(3);
        cart.getItems().add(existing);

        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));
        when(productRepo.findById(oldPid)).thenReturn(Optional.of(oldProduct));

        CartService service = new CartService(cartRepo, productRepo);

        // Update with empty items -> should only release existing reservations
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of());

        var resp = service.updateCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, cart.getItems().size());
        assertEquals(4, oldProduct.getReservedQuantity());
        verify(productRepo).save(argThat(saved -> saved.getId().equals(oldPid) && saved.getReservedQuantity() == 4));
    }

    @Test
    void updateCart_productRepoSaveThrows_wrapsInCartException() {
        CartRepo cartRepo = mock(CartRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);

        Cart cart = new Cart();
        cart.setUserName("alice");
        cart.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(cart));

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 10;
        p.reservedQuantity = 0;
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));
        when(productRepo.save(any(Product.class))).thenThrow(new RuntimeException("db write failed"));

        CartService service = new CartService(cartRepo, productRepo);

        CartUpdateItemDto item = new CartUpdateItemDto();
        item.setProductId(pid.toString());
        item.setQuantity(1);
        CartUpdateRequestDto req = new CartUpdateRequestDto();
        req.setItems(List.of(item));

        assertThrows(CartException.class, () -> service.updateCart("alice", req));
    }
}
