package com.project.spring.skillstack.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

// 프론트에서 보낸 데이터 (Map<String, List<String>>)

@Data
public class HealthCheckRequest {
    private Long userId;
    private Map<String, List<String>> selectedOptions; 
    // key: "심장", "위/장" ... value: ["질문1", "질문4", ...]
}
