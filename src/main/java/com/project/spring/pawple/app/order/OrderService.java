package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderEntity save(OrderDto dto) {
        return orderRepository.save(dto.toEntity());
    }

    public OrderEntity findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. ID: " + id));
    }

    public List<OrderEntity> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public void updateStatus(Long orderId, String status, String trackingNumber) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문이 존재하지 않습니다."));

        order.setStatus(status);

        // 상태가 '배송중'일 경우에만 송장번호 저장
        if ("배송중".equals(status) && trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            order.setTrackingNumber(trackingNumber.trim());
        }

        orderRepository.save(order);
    }
}