package com.project.spring.pawple.app.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentRequestDto {
    private String orderId;
    private String orderName;
    private int amount;
}
