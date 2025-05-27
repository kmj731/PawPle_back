package com.project.spring.skillstack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.HealthCheckRecord;

@Repository
public interface HealthCheckRecordRepository extends JpaRepository<HealthCheckRecord, Long> {
    Optional<HealthCheckRecord> findTopByUserIdOrderByCheckedAtDesc(Long userId);
    Optional<HealthCheckRecord> findTopByPetIdOrderByCheckedAtDesc(Long petId);
}


