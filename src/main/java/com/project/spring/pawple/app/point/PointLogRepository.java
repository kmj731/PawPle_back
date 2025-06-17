package com.project.spring.pawple.app.point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.pawple.app.user.UserEntity;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {

    List<PointLog> findByUserId(Long userId); // 유저별 전체 포인트 기록 조회

    Optional<PointLog> findByUserIdAndReasonAndCreatedBetween(
        Long userId, String reason, LocalDateTime start, LocalDateTime end
    );

    boolean existsByUserIdAndReasonAndCreatedBetween(
        Long userId, String reason, LocalDateTime start, LocalDateTime end
    );

    boolean existsByUserAndReasonAndCreatedBetween(
        UserEntity user, String reason, LocalDateTime start, LocalDateTime end
    );

    boolean existsByUserAndReasonAndCreatedAtBetween(UserEntity user, String reason, LocalDateTime start, LocalDateTime end);
}



