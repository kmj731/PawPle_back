package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.project.spring.pawple.app.auth.CustomUserDetails;

@RestController
@RequestMapping("/store/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Long saveOrder(
        @RequestBody OrderDto dto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long userId = userDetails.getId(); // 인증된 사용자 ID
        OrderEntity saved = orderService.save(userId, dto);
        return saved.getId();
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        OrderEntity order = orderService.findById(id);
        return OrderDto.fromEntity(order);
    }

    @GetMapping
    public List<OrderDto> getOrdersByUser(@RequestParam Long userId) {
        return orderService.findByUserId(userId).stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.findAll().stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
        @PathVariable Long id,
        @RequestParam String status,
        @RequestParam(required = false) String trackingNumber) {
        
        orderService.updateStatus(id, status, trackingNumber);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/delivery")
    public ResponseEntity<OrderDto> updateDeliveryInfo(
            @PathVariable Long id,
            @RequestBody OrderDto dto) {

        OrderEntity updated = orderService.updateDeliveryInfo(id, dto);
        return ResponseEntity.ok(OrderDto.fromEntity(updated));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(
        @RequestBody OrderDto orderDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getId(); // ✅ 로그인 사용자 기준으로 주문 저장
        OrderEntity saved = orderService.save(userId, orderDto);

        return ResponseEntity.ok(saved.getId());
    }

}