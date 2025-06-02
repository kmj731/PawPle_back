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

    // 점수 기준 문항 정의
    private static final Map<String, List<String>> QUESTION_SCORES = Map.of(
        "심장", List.of("심장박동이 불규칙해요", "숨이 가빠요", "기절한 적이 있어요", "쉽게 지쳐요", "없어요"),
        "위/장", List.of("구토를 자주 해요", "설사를 자주 해요", "밥을 잘 안 먹거나 식욕이 줄었어요", "변 상태가 자주 물처럼 묽어요", "없어요"),
        "피부/귀", List.of("피부에서 냄새가 나요", "귀에서 분비물이 나와요", "피부가 빨개요", "가려워서 자주 긁어요", "없어요"),
        "신장/방광", List.of("소변을 자주 봐요", "소변 냄새가 강해요", "소변을 볼 때 힘들어하거나 자주 실수해요", "소변 색이 평소보다 진하거나 붉어요", "없어요"),
        "면역력/호흡기", List.of("기침을 자주 해요", "콧물이 나고 코를 자주 문질러요", "열이 있어요", "숨이 차서 헐떡거려요", "없어요"),
        "치아", List.of("입에서 냄새가 나요", "딱딱한 사료를 잘 못 씹어요", "이가 흔들리거나 빠졌어요", "잇몸이 붓고 피가 나요", "없어요"),
        "뼈/관절", List.of("다리를 절뚝거려요", "계단을 오르기 힘들어해요", "일어나기 힘들어해요", "산책을 싫어해요", "없어요"),
        "눈", List.of("눈꼽이 많이 껴요", "눈이 빨개요", "빛에 민감하게 반응해요", "눈이 뿌옇게 보여요", "없어요"),
        "행동", List.of("기운이 없어요", "짖는 횟수가 줄었어요", "숨는 일이 많아졌어요", "혼자 있으려고 해요", "없어요"),
        "체중 및 비만도", List.of("최근 강아지의 체중이 눈에 띄게 늘었거나 줄었어요", "허리 라인이 잘 안 보이거나 만져지지 않아요", "배를 만졌을 때 갈비뼈가 잘 느껴지지 않아요", "예전보다 덜 움직이고, 활동량이 줄었거나 쉽게 지쳐해요", "없어요")
    );

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

        // ❗ '없어요'와 다른 항목이 동시에 선택되었는지 검사
        for (Map.Entry<String, List<String>> entry : request.getSelectedOptions().entrySet()) {
            List<String> selected = entry.getValue();
            if (selected.contains("없어요") && selected.size() > 1) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", String.format("'%s' 항목에서 '없어요'는 다른 보기와 함께 선택할 수 없습니다.", entry.getKey())
                ));
            }
        }

        // ✅ 감점 방식: 기본 100점 - (2점 * '없어요' 제외 선택 개수)
        int deduction = request.getSelectedOptions().values().stream()
            .flatMap(List::stream)
            .filter(answer -> !"없어요".equals(answer))
            .mapToInt(answer -> 2)
            .sum();

        int totalScore = 100 - deduction;

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
        record.setWarnings(getTopCategories(request));

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
            .map(entry -> entry.getKey().replaceAll("^\\d+\\.\\s*", ""))  // ← 숫자 제거
            .toList();
    }
}
