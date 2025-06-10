package com.project.spring.pawple.app.payment;

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
public class PaymentResultDto {
    private Long paymentId;         // 추가
    private boolean success;
    private String status;          // 추가
    private String message;
    private String transactionId;
    private String paymentMethod;
    private int paidAmount;
}
