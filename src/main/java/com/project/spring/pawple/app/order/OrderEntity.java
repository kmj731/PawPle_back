package com.project.spring.pawple.app.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDER_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 주문한 유저 ID (또는 @ManyToOne 관계로 UserEntity 연동 가능)

    private int totalAmount; // 전체 결제 금액
    private String status;   // 주문 상태: 결제완료, 배송중, 배송완료 등
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    private String recipientName;     // 수령인 이름
    private String recipientPhone;    // 수령인 전화번호
    private String address;           // 주소 (전체 주소 문자열 또는 우편번호+주소 분리도 가능)
    private String deliveryMemo;      // 배송 요청사항 메모 (선택사항)
    private String trackingNumber;
}
