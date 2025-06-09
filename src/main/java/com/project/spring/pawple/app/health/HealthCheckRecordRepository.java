package com.project.spring.pawple.app.health;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCheckRecordRepository extends JpaRepository<HealthCheckRecord, Long> {
    Optional<HealthCheckRecord> findTopByUserIdOrderByCheckedAtDesc(Long userId);
    List<HealthCheckRecord> findByPetId(Long petId);
    Optional<HealthCheckRecord> findTopByPetIdOrderByCheckedAtDesc(Long petId);
    Optional<HealthCheckRecord> findByPetIdAndCheckedAt(Long petId, LocalDateTime checkedAt);
    List<HealthCheckRecord> findByPetIdOrderByCheckedAtDesc(Long petId);

}

