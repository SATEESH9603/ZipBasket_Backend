package com.example.onlinetest.Configuration;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Repo.Cart;
import com.example.onlinetest.Repo.CartRepo;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;

@Configuration
@EnableScheduling
public class ReservationCleanupConfig {

    private static final Logger log = LoggerFactory.getLogger(ReservationCleanupConfig.class);
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    @Value("${reservation.cleanup.hours:6}")
    private int cleanupHours;

    public ReservationCleanupConfig(CartRepo cartRepo, ProductRepo productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void releaseStaleReservations() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(cleanupHours <= 0 ? 6 : cleanupHours);
            for (Cart cart : cartRepo.findAll()) {
                if (cart.getUpdatedAt() == null || cart.getUpdatedAt().isBefore(cutoff)) {
                    cart.getItems().forEach(ci -> {
                        Product p = productRepo.findById(ci.getProduct().getId()).orElse(null);
                        if (p != null) {
                            int curRes = Math.max(0, p.getReservedQuantity());
                            int release = Math.max(0, ci.getQuantity());
                            p.setReservedQuantity(Math.max(0, curRes - release));
                            productRepo.save(p);
                        }
                    });
                    cart.getItems().clear();
                    cartRepo.save(cart);
                }
            }
        } catch (Exception e) {
            log.warn("Reservation cleanup failed: {}", e.getMessage());
        }
    }
}
