package com.project.spring.pawple.app.order;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long userId;
    private int totalAmount;
    private String status;
    private List<OrderItemDto> items;
    private String orderDate;

    public OrderEntity toEntity() {
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .status(status)
                .orderDate(LocalDateTime.now())
                .build();

        List<OrderItemEntity> itemEntities = items.stream()
                .map(OrderItemDto::toEntity)
                .peek(item -> item.setOrder(order))
                .toList();

        order.setItems(itemEntities);
        return order;
    }

    public static OrderDto fromEntity(OrderEntity order) {
        return OrderDto.builder()
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss.SSS")))
                .items(
                    order.getItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .toList()
                )
                .build();
    }
}