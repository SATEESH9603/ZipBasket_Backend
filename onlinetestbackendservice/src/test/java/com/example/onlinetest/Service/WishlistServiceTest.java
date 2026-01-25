package com.example.onlinetest.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.onlinetest.Domain.Dto.WishlistModifyRequestDto;
import com.example.onlinetest.Domain.Exceptions.CartException;
import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;
import com.example.onlinetest.Repo.Wishlist;
import com.example.onlinetest.Repo.WishlistRepo;

class WishlistServiceTest {

    @Test
    void view_existingWishlist_returnsSuccess() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        var resp = svc.view("alice");
        assertTrue(resp.isSuccess());
        assertNotNull(resp.getItems());
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void view_missingWishlist_createsAndSaves() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.empty());
        when(wishlistRepo.save(any(Wishlist.class))).thenAnswer(inv -> {
            Wishlist saved = inv.getArgument(0);
            if (saved.getItems() == null) saved.setItems(new ArrayList<>());
            return saved;
        });

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        var resp = svc.view("alice");
        assertTrue(resp.isSuccess());
        verify(wishlistRepo).save(argThat(x -> "alice".equals(x.getUserName())));
    }

    @Test
    void view_repoThrows_returnsSafeFailureResponse() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        when(wishlistRepo.findByUserName("alice")).thenThrow(new RuntimeException("db down"));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        var resp = svc.view("alice");
        assertFalse(resp.isSuccess());
        assertNotNull(resp.getItems());
        assertEquals(0, resp.getItems().size());
    }

    @Test
    void add_invalidProductId_throwsCartException() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId("not-a-uuid");

        assertThrows(CartException.class, () -> svc.add("alice", req));
    }

    @Test
    void add_productNotFound_returnsFailureResponse() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        UUID pid = UUID.randomUUID();
        when(productRepo.findById(pid)).thenReturn(Optional.empty());

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp = svc.add("alice", req);
        assertFalse(resp.isSuccess());
        assertEquals("Product not found", resp.getMessage());
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void add_outOfStock_doesNotAddButReturnsSuccess() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 1;
        p.reservedQuantity = 1; // availableForDisplay = 0
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp = svc.add("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, w.getItems().size());
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void add_inStock_addsOnce_duplicateDoesNotAddAgain() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 10;
        p.reservedQuantity = 0;
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp1 = svc.add("alice", req);
        assertTrue(resp1.isSuccess());
        assertEquals(1, w.getItems().size());

        var resp2 = svc.add("alice", req);
        assertTrue(resp2.isSuccess());
        assertEquals(1, w.getItems().size());

        verify(wishlistRepo, atLeastOnce()).save(any(Wishlist.class));
    }

    @Test
    void add_repoThrows_wrapsInOrderException() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        UUID pid = UUID.randomUUID();
        when(productRepo.findById(pid)).thenThrow(new RuntimeException("db"));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        assertThrows(OrderException.class, () -> svc.add("alice", req));
    }

    @Test
    void remove_invalidProductId_throwsCartException() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId("not-a-uuid");

        assertThrows(CartException.class, () -> svc.remove("alice", req));
    }

    @Test
    void remove_removesItem_ifPresent() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.quantity = 10;
        p.reservedQuantity = 0;

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        var wi = new com.example.onlinetest.Repo.WishlistItem();
        wi.setWishlist(w);
        wi.setProduct(p);
        w.getItems().add(wi);

        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp = svc.remove("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, w.getItems().size());
        verify(wishlistRepo).save(any(Wishlist.class));
    }

    @Test
    void moveToCart_invalidProductId_throwsCartException() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));

        Cart c = new Cart();
        c.setUserName("alice");
        c.setItems(new ArrayList<>());
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(c));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId("not-a-uuid");

        assertThrows(CartException.class, () -> svc.moveToCart("alice", req));
    }

    @Test
    void moveToCart_inStock_reservesAndAddsToCart_andRemovesFromWishlist() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.name = "P1";
        p.price = BigDecimal.TEN;
        p.quantity = 2;
        p.reservedQuantity = 0;

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        var wi = new com.example.onlinetest.Repo.WishlistItem();
        wi.setWishlist(w);
        wi.setProduct(p);
        w.getItems().add(wi);

        Cart c = new Cart();
        c.setUserName("alice");
        c.setItems(new ArrayList<>());

        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(c));
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp = svc.moveToCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, w.getItems().size());
        assertEquals(1, c.getItems().size());
        assertEquals(1, c.getItems().get(0).getQuantity());
        assertEquals(1, p.getReservedQuantity());

        verify(productRepo).save(argThat(saved -> saved.getReservedQuantity() == 1));
        verify(cartRepo).save(any(Cart.class));
        verify(wishlistRepo).save(any(Wishlist.class));
    }

    @Test
    void moveToCart_outOfStock_onlyRemovesFromWishlist_noCartAdd() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        UUID pid = UUID.randomUUID();
        Product p = new Product();
        p.id = pid;
        p.quantity = 1;
        p.reservedQuantity = 1; // available = 0

        Wishlist w = new Wishlist();
        w.setUserName("alice");
        w.setItems(new ArrayList<>());
        var wi = new com.example.onlinetest.Repo.WishlistItem();
        wi.setWishlist(w);
        wi.setProduct(p);
        w.getItems().add(wi);

        Cart c = new Cart();
        c.setUserName("alice");
        c.setItems(new ArrayList<>());

        when(wishlistRepo.findByUserName("alice")).thenReturn(Optional.of(w));
        when(cartRepo.findByUserName("alice")).thenReturn(Optional.of(c));
        when(productRepo.findById(pid)).thenReturn(Optional.of(p));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(pid.toString());

        var resp = svc.moveToCart("alice", req);
        assertTrue(resp.isSuccess());
        assertEquals(0, w.getItems().size());
        assertEquals(0, c.getItems().size());
        verify(productRepo, never()).save(any());
    }

    @Test
    void moveToCart_repoThrows_wrapsInOrderException() {
        WishlistRepo wishlistRepo = mock(WishlistRepo.class);
        ProductRepo productRepo = mock(ProductRepo.class);
        CartRepo cartRepo = mock(CartRepo.class);

        when(wishlistRepo.findByUserName("alice")).thenThrow(new RuntimeException("db"));

        WishlistService svc = new WishlistService(wishlistRepo, productRepo, cartRepo);

        WishlistModifyRequestDto req = new WishlistModifyRequestDto();
        req.setProductId(UUID.randomUUID().toString());

        assertThrows(OrderException.class, () -> svc.moveToCart("alice", req));
    }
}
