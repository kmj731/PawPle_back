package com.project.spring.skillstack.dto;

import java.util.List;

import lombok.Data;

@Data
public class HealthCheckResultResponse {
    private int score;
    private String status; // 양호 / 경고 / 위험
    private List<String> warnings; // 주의할 항목 top 3
}
