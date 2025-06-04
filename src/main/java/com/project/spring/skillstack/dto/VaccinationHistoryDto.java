package com.project.spring.skillstack.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 접종 이력 조회용
public class VaccinationHistoryDto {
    private int step;
    private String vaccineName;
    private LocalDate vaccinatedAt;
    private LocalDate nextVaccinationDate;
    private String dday;
}
