package com.project.spring.pawple.app.point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointLogRepository pointLogRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    /**
     * 하루에 한 번만 미션 포인트 지급
     */
    @Transactional
    public ResponseEntity<?> completeMission(Long userId, List<MissionType> missions) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (missions == null || missions.isEmpty()) {
            return ResponseEntity.badRequest().body("하나 이상의 미션을 선택해주세요.");
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 오늘 이미 받은 경우 방지
        boolean alreadyReceived = pointLogRepository.existsByUserAndReasonAndCreatedBetween(
            user, "DAILY_MISSION", startOfDay, endOfDay);

        if (alreadyReceived) {
            return ResponseEntity.badRequest().body("오늘은 이미 미션 포인트를 받았어요!");
        }

        int randomPoint = 1 + random.nextInt(10); // 1~10점 랜덤

        PointLog log = PointLog.builder()
            .user(user)
            .amount(randomPoint)
            .reason("DAILY_MISSION")
            .type("EARN")
            .build();

        pointLogRepository.save(log);
        user.addPoint(randomPoint);
        userRepository.save(user);

        return ResponseEntity.ok("✅ 미션 완료! " + randomPoint + "점이 적립되었습니다!");
    }
}

