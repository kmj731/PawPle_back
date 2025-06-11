package com.project.spring.pawple.app.order;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ORDER_ITEM_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productName;
    private int quantity;
    private int price; // 주문 당시 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}