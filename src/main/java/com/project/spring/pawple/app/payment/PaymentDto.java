package com.project.spring.skillstack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long productId;
    private Long userId;
    private int amount;
    private String paymentMethod; // 예: KAKAOPAY, CARD 등
}