package com.project.spring.skillstack.controller.store;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.PaymentDto;
import com.project.spring.skillstack.dto.PaymentResultDto;
import com.project.spring.skillstack.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 처리
    @PostMapping("/process")
    public PaymentResultDto processPayment(@RequestBody PaymentDto dto) {
        return paymentService.processPayment(dto);
    }

    // 환불 처리
    @PostMapping("/refund/{paymentId}")
    public PaymentResultDto refundPayment(@PathVariable Long paymentId) {
        return paymentService.refundPayment(paymentId);
    }
}
