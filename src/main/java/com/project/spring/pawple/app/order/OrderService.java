package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderEntity save(OrderDto dto) {
        return orderRepository.save(dto.toEntity());
    }
}