package com.project.spring.pawple.application.health;

import java.util.List;
import lombok.Data;

@Data
public class HealthCheckResultResponse {
    private int score;
    private String status;
    private List<String> warnings;
}
