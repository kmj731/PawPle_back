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
    private Long id;
    private Long userId;
    private int totalAmount;
    private String status;
    private List<OrderItemDto> items;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private String deliveryMemo;
    private String trackingNumber;
    private String orderDate;

    public OrderEntity toEntity() {
        if (this.id != null) {
            throw new IllegalStateException("toEntity()는 새 주문 저장에만 사용됩니다.");
        }

        OrderEntity order = OrderEntity.builder()
                .totalAmount(totalAmount)
                .status(status != null ? status : "결제완료")
                .orderDate(LocalDateTime.now())
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .address(address)
                .deliveryMemo(deliveryMemo)
                .trackingNumber(trackingNumber)
                .build();

        List<OrderItemEntity> itemEntities = items.stream()
                .map(OrderItemDto::toEntity)
                .peek(item -> item.setOrder(order))
                .toList();

        order.setItems(itemEntities);
        return order;
    }

    public static OrderDto fromEntity(OrderEntity order) {
        String formattedDate = "-";
        try {
            formattedDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss.SSS"));
        } catch (Exception e) {
            // 생략 가능
        }

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(formattedDate)
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .address(order.getAddress())
                .deliveryMemo(order.getDeliveryMemo())
                .trackingNumber(order.getTrackingNumber())
                .items(order.getItems().stream().map(OrderItemDto::fromEntity).toList())
                .build();
    }
}
