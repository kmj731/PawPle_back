package com.project.spring.pawple.app.vaccine;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.pawple.app.user.UserEntity;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByPetIdOrderByVaccinatedAtDesc(Long petId);
    Optional<VaccinationRecord> findByPetIdAndVaccinatedAt(Long petId, LocalDate vaccinatedAt);

}