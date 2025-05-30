
package com.project.spring.skillstack.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HealthCheckRequest {
    private Long userId;
    private Long petId; // 추가됨
    private Map<String, List<String>> selectedOptions;
}
