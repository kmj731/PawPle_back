package com.project.spring.pawple.app.health;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class HealthCheckRecordDto {
    private Long id;
    private LocalDateTime checkedAt;
    private int totalScore;
    private String resultStatus;
    private List<String> warnings;
    private List<HealthCheckDetailDto> details;
}