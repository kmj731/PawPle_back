package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;
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
}