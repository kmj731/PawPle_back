package com.project.spring.skillstack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.skillstack.entity.VaccinationRecord;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByPetIdOrderByVaccinatedAtDesc(Long petId);
}