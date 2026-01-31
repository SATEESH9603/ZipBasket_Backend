package com.example.onlinetest.Service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Domain.Dto.CartUpdateRequestDto;
import com.example.onlinetest.Domain.Dto.CartUpdateResponseDto;
import com.example.onlinetest.Domain.Dto.CartViewResponseDto;
import com.example.onlinetest.Domain.Exceptions.CartException;
import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartItem;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;

@Service
public class CartService implements ICartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    public CartService(CartRepo cartRepo, ProductRepo productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    @Override
    public CartViewResponseDto viewCart(String userName) {
        try {
            Cart cart = cartRepo.findByUserName(userName).orElseGet(() -> {
                Cart c = new Cart();
                c.setUserName(userName);
                return cartRepo.save(c);
            });
            return Mapper.toCartViewResponseDto(cart);
        } catch (Exception e) {
            throw new CartException("Failed to fetch cart for user: " + userName, e);
        }
    }

    @Override
    @Transactional
    public CartUpdateResponseDto updateCart(String userName, CartUpdateRequestDto request) {
        try {
            Cart cart = cartRepo.findByUserName(userName).orElseGet(() -> {
                Cart c = new Cart();
                c.setUserName(userName);
                return cartRepo.save(c);
            });

            // Release previous reservations for this cart
            for (CartItem existing : cart.getItems()) {
                Product p = productRepo.findById(existing.getProduct().getId()).orElse(null);
                if (p != null) {
                    int curRes = Math.max(0, p.getReservedQuantity());
                    int release = Math.max(0, existing.getQuantity());
                    p.setReservedQuantity(Math.max(0, curRes - release));
                    productRepo.save(p);
                }
            }

            cart.getItems().clear();

            // Recreate items and reserve stock
            for (var i : request.getItems()) {
                if (i.getQuantity() <= 0) continue;
                UUID productId = UUID.fromString(i.getProductId());
                Product product = productRepo.findById(productId).orElse(null);
                if (product == null) continue;
                int available = Math.max(0, product.getQuantity() - product.getReservedQuantity());
                int desired = i.getQuantity();
                int finalQty = Math.min(desired, available);
                if (finalQty <= 0) continue;

                product.setReservedQuantity(product.getReservedQuantity() + finalQty);
                productRepo.save(product);

                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(finalQty);
                cart.getItems().add(item);
            }

            cartRepo.save(cart);
            CartUpdateResponseDto response = new CartUpdateResponseDto();
            response.setSuccess(true);
            response.setMessage("Cart updated successfully");
            return response;
        } catch (IllegalArgumentException iae) {
            throw new CartException("Invalid product ID in cart update", iae);
        } catch (Exception e) {
            throw new CartException("Failed to update cart for user: " + userName, e);
        }
    }
}
