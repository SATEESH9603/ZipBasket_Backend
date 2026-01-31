package com.example.onlinetest.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.OrderDetailDto;
import com.example.onlinetest.Domain.Dto.OrderSummaryDto;
import com.example.onlinetest.Domain.Dto.OrdersPageDto;
import com.example.onlinetest.Service.IOrderService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "User and Seller order listing and actions")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<OrderSummaryDto>> list(@PathVariable String username) {
        List<OrderSummaryDto> resp = orderService.list(username);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/{username}/{orderId}")
    public ResponseEntity<OrderDetailDto> get(@PathVariable String username, @PathVariable UUID orderId) {
        OrderDetailDto resp = orderService.get(username, orderId);
        return new ResponseEntity<>(resp, resp != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{username}/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String username, @PathVariable UUID orderId) {
        boolean ok = orderService.cancel(username, orderId);
        return new ResponseEntity<>(ok ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{username}/{orderId}/return")
    public ResponseEntity<Void> requestReturn(@PathVariable String username, @PathVariable UUID orderId) {
        boolean ok = orderService.requestReturn(username, orderId);
        return new ResponseEntity<>(ok ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> listBySeller(@PathVariable UUID sellerId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        if (page != null || size != null) {
            int p = page == null ? 1 : Math.max(1, page);
            int s = size == null ? 10 : Math.max(1, size);
            List<OrderSummaryDto> items = orderService.listBySellerPaged(sellerId, p, s);
            OrdersPageDto<OrderSummaryDto> dto = new OrdersPageDto<>();
            dto.setItems(items);
            dto.setPage(p);
            dto.setTotalPages(s); // note: service could also return total pages if repo supports
            dto.setTotalItems(items.size());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        List<OrderSummaryDto> resp = orderService.listBySeller(sellerId);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/seller/{sellerId}/details")
    public ResponseEntity<List<OrderDetailDto>> listDetailsBySeller(@PathVariable UUID sellerId) {
        List<OrderDetailDto> resp = orderService.listDetailsBySeller(sellerId);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
