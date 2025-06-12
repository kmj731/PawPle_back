package com.project.spring.pawple.app.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 주문 취소 = 삭제 대신 status 변경
    public void cancelOrder(Long id) {
        OrderEntity order = findById(id);
        order.setStatus("주문취소");
        orderRepository.save(order);
    }

    public void updateStatus(Long orderId, String status, String trackingNumber) {
        OrderEntity order = findById(orderId);
        order.setStatus(status);

        if ("배송중".equals(status) && trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            order.setTrackingNumber(trackingNumber.trim());
        } else if (!"배송중".equals(status)) {
            order.setTrackingNumber(null); // 다른 상태에서는 송장 제거 (선택)
        }

        orderRepository.save(order);
    }

    public OrderEntity updateDeliveryInfo(Long orderId, OrderDto dto) {
        OrderEntity order = findById(orderId);

        // 필드만 수정
        order.setRecipientName(dto.getRecipientName());
        order.setRecipientPhone(dto.getRecipientPhone());
        order.setAddress(dto.getAddress());

        return orderRepository.save(order);
    }
}
