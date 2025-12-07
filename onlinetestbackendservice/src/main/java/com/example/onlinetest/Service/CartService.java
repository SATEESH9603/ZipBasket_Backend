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

            // Build new items from request using streams
            cart.getItems().clear();
            request.getItems().stream()
                .filter(i -> i.getQuantity() > 0)
                .map(i -> {
                    try {
                        var productId = UUID.fromString(i.getProductId());
                        return productRepo.findById(productId)
                                .map(product -> {
                                    CartItem item = new CartItem();
                                    item.setCart(cart);
                                    item.setProduct(product);
                                    item.setQuantity(i.getQuantity());
                                    return item;
                                })
                                .orElse(null);
                    } catch (IllegalArgumentException ex) {
                        // invalid UUID -> skip, will be handled by catch below
                        throw ex;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .forEach(cart.getItems()::add);

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
