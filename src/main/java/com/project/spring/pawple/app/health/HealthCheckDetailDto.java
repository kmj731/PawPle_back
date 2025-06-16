package com.project.spring.pawple.app.health;

import java.util.List;

import lombok.Data;

@Data
public class HealthCheckDetailDto {
    private String category;
    private int score;
    private List<String> selectedOptions;
}