package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Long saveOrder(@RequestBody OrderDto dto) {
        OrderEntity saved = orderService.save(dto);
        return saved.getId(); // 주문 번호 반환
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
}