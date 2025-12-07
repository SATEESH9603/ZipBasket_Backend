package com.example.onlinetest.Service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Domain.Dto.WishlistModifyRequestDto;
import com.example.onlinetest.Domain.Dto.WishlistModifyResponseDto;
import com.example.onlinetest.Domain.Dto.WishlistViewResponseDto;
import com.example.onlinetest.Domain.Exceptions.CartException;
import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartItem;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;
import com.example.onlinetest.Repo.Wishlist;
import com.example.onlinetest.Repo.WishlistItem;
import com.example.onlinetest.Repo.WishlistRepo;

@Service
public class WishlistService implements IWishlistService {

    private final WishlistRepo wishlistRepo;
    private final ProductRepo productRepo;
    private final CartRepo cartRepo;

    public WishlistService(WishlistRepo wishlistRepo, ProductRepo productRepo, CartRepo cartRepo) {
        this.wishlistRepo = wishlistRepo;
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public WishlistViewResponseDto view(String userName) {
        try {
            Wishlist wishlist = wishlistRepo.findByUserName(userName).orElseGet(() -> {
                Wishlist w = new Wishlist();
                w.setUserName(userName);
                return wishlistRepo.save(w);
            });
            return Mapper.toWishlistViewResponseDto(wishlist);
        } catch (Exception e) {
            throw new OrderException("Failed to view wishlist for user: " + userName, e);
        }
    }

    @Override
    @Transactional
    public WishlistModifyResponseDto add(String userName, WishlistModifyRequestDto request) {
        try {
            Wishlist wishlist = wishlistRepo.findByUserName(userName).orElseGet(() -> {
                Wishlist w = new Wishlist();
                w.setUserName(userName);
                return wishlistRepo.save(w);
            });
            UUID productId = UUID.fromString(request.getProductId());
            Product product = productRepo.findById(productId).orElse(null);
            if (product == null) {
                WishlistModifyResponseDto resp = new WishlistModifyResponseDto();
                resp.setSuccess(false);
                resp.setMessage("Product not found");
                return resp;
            }
            boolean exists = wishlist.getItems().stream().anyMatch(i -> i.getProduct().getId().equals(product.getId()));
            if (!exists) {
                WishlistItem item = new WishlistItem();
                item.setWishlist(wishlist);
                item.setProduct(product);
                wishlist.getItems().add(item);
                wishlistRepo.save(wishlist);
            }
            WishlistModifyResponseDto resp = new WishlistModifyResponseDto();
            resp.setSuccess(true);
            resp.setMessage("Item added to wishlist");
            return resp;
        } catch (IllegalArgumentException iae) {
            throw new CartException("Invalid product ID in wishlist add", iae);
        } catch (Exception e) {
            throw new OrderException("Failed to add to wishlist for user: " + userName, e);
        }
    }

    @Override
    @Transactional
    public WishlistModifyResponseDto remove(String userName, WishlistModifyRequestDto request) {
        try {
            Wishlist wishlist = wishlistRepo.findByUserName(userName).orElseGet(() -> {
                Wishlist w = new Wishlist();
                w.setUserName(userName);
                return wishlistRepo.save(w);
            });
            UUID productId = UUID.fromString(request.getProductId());
            wishlist.getItems().removeIf(i -> i.getProduct() != null && productId.equals(i.getProduct().getId()));
            wishlistRepo.save(wishlist);
            WishlistModifyResponseDto resp = new WishlistModifyResponseDto();
            resp.setSuccess(true);
            resp.setMessage("Item removed from wishlist");
            return resp;
        } catch (IllegalArgumentException iae) {
            throw new CartException("Invalid product ID in wishlist remove", iae);
        } catch (Exception e) {
            throw new OrderException("Failed to remove from wishlist for user: " + userName, e);
        }
    }

    @Override
    @Transactional
    public WishlistModifyResponseDto moveToCart(String userName, WishlistModifyRequestDto request) {
        try {
            Wishlist wishlist = wishlistRepo.findByUserName(userName).orElseGet(() -> {
                Wishlist w = new Wishlist();
                w.setUserName(userName);
                return wishlistRepo.save(w);
            });
            Cart cart = cartRepo.findByUserName(userName).orElseGet(() -> {
                Cart c = new Cart();
                c.setUserName(userName);
                return cartRepo.save(c);
            });
            UUID productId = UUID.fromString(request.getProductId());
            // remove from wishlist
            wishlist.getItems().removeIf(i -> i.getProduct() != null && productId.equals(i.getProduct().getId()));
            // add to cart (quantity = 1)
            Product product = productRepo.findById(productId).orElse(null);
            if (product != null) {
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(1);
                cart.getItems().add(item);
                cartRepo.save(cart);
            }
            wishlistRepo.save(wishlist);
            WishlistModifyResponseDto resp = new WishlistModifyResponseDto();
            resp.setSuccess(true);
            resp.setMessage("Item moved to cart");
            return resp;
        } catch (IllegalArgumentException iae) {
            throw new CartException("Invalid product ID in move to cart", iae);
        } catch (Exception e) {
            throw new OrderException("Failed to move to cart for user: " + userName, e);
        }
    }
}
