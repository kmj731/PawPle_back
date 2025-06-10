package com.project.spring.skillstack.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.PaymentRepository;
import com.project.spring.skillstack.dto.PaymentDto;
import com.project.spring.skillstack.dto.PaymentResultDto;
import com.project.spring.skillstack.entity.PaymentEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResultDto processPayment(PaymentDto dto) {
        // 테스트용 결제 처리
        boolean success = dto.getAmount() > 0; // 임시 성공 조건

        PaymentEntity payment = PaymentEntity.builder()
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .transactionId(UUID.randomUUID().toString())
                .paidAt(LocalDateTime.now())
                .success(success)
                .failReason(success ? null : "결제 실패: 금액 오류")
                .build();

        paymentRepository.save(payment);

        return PaymentResultDto.builder()
                .success(success)
                .message(success ? "결제 성공" : "결제 실패")
                .transactionId(payment.getTransactionId())
                .paymentMethod(dto.getPaymentMethod())
                .paidAmount(dto.getAmount())
                .build();
    }

    public PaymentResultDto refundPayment(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("결제 내역이 존재하지 않습니다."));

        if (!"PAID".equals(payment.getStatus())) {
            throw new RuntimeException("이미 환불되었거나 결제되지 않은 내역입니다.");
        }

        payment.setStatus("REFUNDED");
        payment.setRefundedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return PaymentResultDto.builder()
                .paymentId(payment.getId())
                .status("REFUNDED")
                .message("환불 처리 완료")
                .build();
    }
}

