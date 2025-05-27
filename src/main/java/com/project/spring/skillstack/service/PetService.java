package com.project.spring.skillstack.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.entity.HealthCheckRecord;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.repository.HealthCheckRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final HealthCheckRecordRepository healthCheckRecordRepository;

    public long getPetCheckupDday(Long petId) {
        PetEntity pet = petRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않아요"));

        // 마지막 검진 기록 가져오기
        HealthCheckRecord lastRecord = healthCheckRecordRepository.findTopByUserIdOrderByCheckedAtDesc(petId)
            .orElseThrow(() -> new IllegalStateException("아직 검진 기록이 없어요! 먼저 등록해 주세요."));

        LocalDate lastCheckDate = lastRecord.getCheckedAt().toLocalDate();
        LocalDate nextCheckDate = HealthCheckScheduler.calculateNextCheckupDate(lastCheckDate, pet.getPetAge());

        return HealthCheckScheduler.calculateDday(nextCheckDate);
    }
}
