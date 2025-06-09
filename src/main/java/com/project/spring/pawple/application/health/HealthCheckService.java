package com.project.spring.pawple.application.health;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

// 항목별 건강 상태 판단 로직

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final HealthCheckRecordRepository healthCheckRecordRepository;

    // 임시 메모리 저장소
    private final Map<Long, HealthCheckResultResponse> resultMap = new HashMap<>();

    // 결과 불러오기
    public HealthCheckResultResponse getResult(Long userId) {
        return resultMap.get(userId);
    }

    // 결과 처리
    public void processCheck(HealthCheckRequest request) {
        int score = calculateScore(request.getSelectedOptions());
        String status = evaluateStatus(score);

        HealthCheckResultResponse response = new HealthCheckResultResponse();
        response.setScore(score);
        response.setStatus(status);

        // 증상 있는 카테고리만 warnings 추가
        List<String> warnings = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : request.getSelectedOptions().entrySet()) {
            boolean hasSymptom = entry.getValue().stream().anyMatch(s -> !s.equals("없어요"));
            if (hasSymptom) warnings.add(entry.getKey());
        }
        response.setWarnings(warnings);

        resultMap.put(request.getUserId(), response);
    }

    // 점수 계산
    private int calculateScore(Map<String, List<String>> selectedOptions) {
        int total = 0;
        for (List<String> answers : selectedOptions.values()) {
            long symptomCount = answers.stream().filter(ans -> !ans.equals("없어요")).count();
            int categoryScore = Math.max(0, 10 - (int) symptomCount * 2);
            total += categoryScore;
        }
        return total;
    }

    // 상태 판단
    private String evaluateStatus(int score) {
        if (score >= 80) return "양호";
        if (score >= 50) return "경고";
        else { return "위험"; }
    }
}
