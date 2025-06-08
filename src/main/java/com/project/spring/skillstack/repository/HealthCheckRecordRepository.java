package com.project.spring.skillstack.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.HealthCheckRecord;

@Repository
public interface HealthCheckRecordRepository extends JpaRepository<HealthCheckRecord, Long> {
    Optional<HealthCheckRecord> findTopByUserIdOrderByCheckedAtDesc(Long userId);
    List<HealthCheckRecord> findByPetId(Long petId);
    Optional<HealthCheckRecord> findTopByPetIdOrderByCheckedAtDesc(Long petId);
    Optional<HealthCheckRecord> findByPetIdAndCheckedAt(Long petId, LocalDateTime checkedAt);
    List<HealthCheckRecord> findByPetIdOrderByCheckedAtDesc(Long petId);

}

