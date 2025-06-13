package com.project.spring.pawple.app.vaccine;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VaccinationHistoryAllDto {
    private Long petId;
    private String petName;
    private int step;
    private String vaccineName;
    private LocalDate vaccinatedAt;
    private LocalDate nextVaccinationDate;
    private String dday;
}
