package com.project.spring.pawple.application.pet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PetService {

    private final PetRepository petRepository;

    public PetDto getPetById(Long petId) {
    PetEntity pet = petRepository.findById(petId)
        .orElseThrow(() -> new RuntimeException("Pet not found"));
    return new PetDto(pet);
}

    public LocalDate calculateNextHealthCheckDateByLastCheck(LocalDate checkedAt, int age) {
    if (checkedAt == null || age < 0) {
        return null;
    }

    int intervalDays;
    if (age < 1) {
        intervalDays = 14;
    } else if (age <= 7) {
        intervalDays = 365;
    } else if (age <= 12) {
        intervalDays = 180;
    } else {
        intervalDays = 90;
    }

    return checkedAt.plusDays(intervalDays);
}

    public long calculateDDay(LocalDate nextCheckDate) {
        if (nextCheckDate == null) return -1;
        return ChronoUnit.DAYS.between(LocalDate.now(), nextCheckDate);
    }
}
