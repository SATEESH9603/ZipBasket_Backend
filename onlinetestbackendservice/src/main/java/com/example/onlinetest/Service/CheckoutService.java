package com.example.onlinetest.Service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Dto.CheckoutRequestDto;
import com.example.onlinetest.Domain.Dto.CheckoutResponseDto;
import com.example.onlinetest.Domain.Exceptions.OrderException;
import com.example.onlinetest.Repo.Address;
import com.example.onlinetest.Repo.AddressRepo;
import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartItem;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Order;
import com.example.onlinetest.Repo.OrderItem;
import com.example.onlinetest.Repo.OrderRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;
import com.example.onlinetest.Repo.ShippingMethod;
import com.example.onlinetest.Repo.UserRepo;

@Service
public class CheckoutService implements ICheckoutService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final AddressRepo addressRepo;
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;

    public CheckoutService(CartRepo cartRepo, ProductRepo productRepo, AddressRepo addressRepo, OrderRepo orderRepo, UserRepo userRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.addressRepo = addressRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public CheckoutResponseDto checkout(CheckoutRequestDto request) {
        try {
            CheckoutResponseDto resp = new CheckoutResponseDto();
            Cart cart = cartRepo.findByUserName(request.getUserName()).orElse(null);
            if (cart == null || cart.getItems().isEmpty()) {
                resp.setSuccess(false);
                resp.setMessage("Cart is empty");
                return resp;
            }
            Address shipping = addressRepo.findById(request.getShippingAddressId()).orElse(null);
            Address billing = addressRepo.findById(request.getBillingAddressId()).orElse(null);
            if (shipping == null || billing == null) {
                resp.setSuccess(false);
                resp.setMessage("Invalid address selection");
                return resp;
            }
            Order order = new Order();
            order.setUser(userRepo.findByUsername(request.getUserName()).orElse(null));
            order.setShippingAddress(shipping);
            order.setBillingAddress(billing);
            try {
                order.setShippingMethod(ShippingMethod.valueOf(request.getShippingMethod().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                resp.setSuccess(false);
                resp.setMessage("Invalid shipping method");
                return resp;
            }
            BigDecimal subtotal = BigDecimal.ZERO;
            for (CartItem ci : cart.getItems()) {
                Product product = productRepo.findById(ci.getProduct().getId()).orElse(null);
                if (product == null) continue;
                // Confirm reservation exists and finalize: decrement quantity and reserved
                int reserved = Math.max(0, product.getReservedQuantity());
                if (reserved < ci.getQuantity()) {
                    resp.setSuccess(false);
                    resp.setMessage("Reserved stock missing for product: " + product.getName());
                    return resp;
                }
                int available = Math.max(0, product.getQuantity());
                if (available < ci.getQuantity()) {
                    resp.setSuccess(false);
                    resp.setMessage("Insufficient stock for product: " + product.getName());
                    return resp;
                }
                product.setReservedQuantity(reserved - ci.getQuantity());
                product.setQuantity(available - ci.getQuantity());
                productRepo.save(product);

                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(product);
                oi.setQuantity(ci.getQuantity());
                oi.setUnitPrice(product.getPrice());
                order.getItems().add(oi);
                subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            }
            order.setSubtotal(subtotal);
            BigDecimal shippingCost = switch (order.getShippingMethod()) {
                case STANDARD -> BigDecimal.valueOf(5);
                case EXPRESS -> BigDecimal.valueOf(15);
                case OVERNIGHT -> BigDecimal.valueOf(30);
            };
            order.setShippingCost(shippingCost);
            order.setTotal(subtotal.add(shippingCost));
            Order saved = orderRepo.save(order);
            // Clear cart
            cart.getItems().clear();
            cartRepo.save(cart);
            resp.setSuccess(true);
            resp.setMessage("Order placed successfully");
            resp.setOrderId(saved.getId());
            resp.setTotal(saved.getTotal());
            return resp;
        } catch (Exception e) {
            throw new OrderException("Checkout failed for user: " + request.getUserName(), e);
        }
    }
}
