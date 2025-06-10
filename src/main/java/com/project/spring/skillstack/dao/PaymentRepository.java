package com.project.spring.skillstack.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.skillstack.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByUserId(Long userId);
    List<PaymentEntity> findByStatus(String status);

}
