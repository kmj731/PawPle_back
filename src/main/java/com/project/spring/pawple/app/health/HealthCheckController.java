package com.project.spring.pawple.app.health;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.project.spring.pawple.app.auth.CustomUserDetails;
import com.project.spring.pawple.app.pet.PetEntity;
import com.project.spring.pawple.app.pet.PetRepository;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private HealthCheckRecordRepository recordRepository;

    private final HealthCheckRecordRepository healthCheckRecordRepository;

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

        int totalScore = calculateTotalScore(request.getSelectedOptions());
        String status = determineStatus(totalScore);


        // 기록 저장
        HealthCheckRecord record = new HealthCheckRecord();
        record.setUserId(optionalUser.get().getId());
        record.setPet(pet);
        record.setCheckedAt(LocalDateTime.now());
        record.setTotalScore(totalScore);
        record.setResultStatus(status);
        record.setWarnings(new ArrayList<>(getTopCategories(request)));


        recordRepository.save(record);

        // 결과 응답
        HealthCheckResultResponse response = new HealthCheckResultResponse();
        response.setScore(totalScore);
        response.setStatus(status);
        record.setWarnings(new ArrayList<>(getTopCategories(request)));


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
            .collect(Collectors.toList());
    }


    @PutMapping("/update/{recordId}")
    public ResponseEntity<String> updateByRecordId(
    @PathVariable Long recordId,
    @RequestBody HealthCheckRequest request,
    @AuthenticationPrincipal UserDetails userDetails
) {
    String username = userDetails.getUsername();
    Long userId = userRepository.findByName(username)
        .orElseThrow(() -> new RuntimeException("로그인한 유저 정보를 찾을 수 없습니다."))
        .getId();

    HealthCheckRecord record = healthCheckRecordRepository.findById(recordId)
        .orElseThrow(() -> new RuntimeException("기록을 찾을 수 없습니다."));

    // 사용자가 자신의 반려동물 기록만 수정할 수 있도록 검증
    if (!record.getPet().getOwner().getId().equals(userId)) {
        throw new RuntimeException("권한이 없습니다.");
    }

    // '없어요' 체크 검사
    for (Map.Entry<String, List<String>> entry : request.getSelectedOptions().entrySet()) {
        List<String> selected = entry.getValue();
        if (selected.contains("없어요") && selected.size() > 1) {
            return ResponseEntity.badRequest().body(
                String.format("'%s' 항목에서 '없어요'는 다른 보기와 함께 선택할 수 없습니다.", entry.getKey())
            );
        }
    }

    // 기존 리스트를 새로운 리스트로 대체
    // ✅ 누락된 항목도 기존 점수 유지
    Map<String, HealthCheckDetail> existingMap = record.getDetails().stream()
    .collect(Collectors.toMap(HealthCheckDetail::getCategory, d -> d));

    List<HealthCheckDetail> newDetails = new ArrayList<>();
    for (String category : QUESTION_SCORES.keySet()) {
    int score = request.getAnswers().getOrDefault(
        category,
        existingMap.containsKey(category) ? existingMap.get(category).getScore() : 0
    );
    HealthCheckDetail detail = HealthCheckDetail.builder()
        .record(record)
        .category(category)
        .score(score)
        .build();
    newDetails.add(detail);
    }
    record.getDetails().clear();
    record.getDetails().addAll(newDetails);




    // 점수 재계산 및 상태 갱신
    int totalScore = calculateTotalScore(request.getSelectedOptions());
    String status = determineStatus(totalScore);
    List<String> warnings = getTopCategories(request);

    record.setTotalScore(totalScore);
    record.setResultStatus(status);
    record.setWarnings(warnings);

    healthCheckRecordRepository.save(record);
    return ResponseEntity.ok("기록이 성공적으로 수정되었습니다!");
}

    


    @DeleteMapping("/delete/{recordId}")
    public ResponseEntity<String> deleteByRecordId(
    @PathVariable Long recordId,
    @AuthenticationPrincipal UserDetails userDetails
) {
    String username = userDetails.getUsername();
    Long userId = userRepository.findByName(username)
        .orElseThrow(() -> new RuntimeException("로그인한 유저 정보를 찾을 수 없습니다."))
        .getId();

    HealthCheckRecord record = healthCheckRecordRepository.findById(recordId)
        .orElseThrow(() -> new RuntimeException("기록을 찾을 수 없습니다."));

    if (!record.getPet().getOwner().getId().equals(userId)) {
        return ResponseEntity.status(403).body("해당 기록에 대한 삭제 권한이 없습니다.");
    }

    healthCheckRecordRepository.delete(record);
    return ResponseEntity.ok("건강 기록이 성공적으로 삭제되었습니다!");
    }



    private int calculateTotalScore(Map<String, List<String>> selectedOptions) {
        return 100 - selectedOptions.values().stream()
            .flatMap(List::stream)
            .filter(answer -> !"없어요".equals(answer))
            .mapToInt(answer -> 2)
            .sum();
    }

    private String determineStatus(int score) {
        if (score >= 70) return "양호";
            else if (score >= 40) return "경고";
                else return "위험";
    }


}
