package com.project.spring.skillstack.controller.health;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.HealthCheckRequest;
import com.project.spring.skillstack.dto.HealthCheckResultResponse;
import com.project.spring.skillstack.entity.HealthCheckRecord;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.repository.HealthCheckRecordRepository;
import com.project.spring.skillstack.service.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private HealthCheckRecordRepository recordRepository;

    /**
     * 건강검진 결과 저장
     */
    @PostMapping("/submit")
    @Transactional
    public ResponseEntity<?> submitCheck(
            @RequestBody HealthCheckRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "사용자 없음"));
        }

        Optional<PetEntity> optionalPet = petRepository.findById(request.getPetId());
        if (optionalPet.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "해당 반려동물 없음"));
        }

        PetEntity pet = optionalPet.get();

        // 총점 계산 (예시: 선택한 답변 수 * 10점)
        int totalScore = request.getSelectedOptions().values().stream()
                .mapToInt(list -> list.size() * 10)
                .sum();

        String status;
        if (totalScore >= 70) status = "양호";
        else if (totalScore >= 40) status = "경고";
        else status = "위험";

        // 기록 저장
        HealthCheckRecord record = new HealthCheckRecord();
        record.setUserId(optionalUser.get().getId());
        record.setPet(pet);
        record.setCheckedAt(LocalDateTime.now());
        record.setTotalScore(totalScore);
        record.setResultStatus(status);

        recordRepository.save(record);

        // 결과 응답
        HealthCheckResultResponse response = new HealthCheckResultResponse();
        response.setScore(totalScore);
        response.setStatus(status);
        response.setWarnings(getTopCategories(request));

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 펫의 건강검진 이력 조회
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getRecordsByPet(@PathVariable Long petId) {
        List<HealthCheckRecord> records = recordRepository.findByPetId(petId);
        return ResponseEntity.ok(records);
    }

    /**
     * 주의가 필요한 항목 top 3 리턴 (선택된 항목 수 기준 정렬)
     */
    private List<String> getTopCategories(HealthCheckRequest request) {
        return request.getSelectedOptions().entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }
}
