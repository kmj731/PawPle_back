package com.project.spring.pawple.app.pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class PetManagerService {

    @Autowired
    private PetRepository petRepository;

    @Transactional
    public PetEntity updatePet(Long petId, PetEntity updatedPet) {
        int retryCount = 3;  // 최대 3번까지 재시도

        while (retryCount > 0) {
            try {
                // 기존 데이터 조회
                PetEntity existingPet = petRepository.findById(petId)
                    .orElseThrow(() -> new RuntimeException("Pet not found"));

                // 기존 데이터를 수정
                existingPet.setPetName(updatedPet.getPetName());
                existingPet.setPetType(updatedPet.getPetType());
                existingPet.setPetAge(updatedPet.getPetAge());
                existingPet.setPetBreed(updatedPet.getPetBreed());
                existingPet.setPetGender(updatedPet.getPetGender());
                existingPet.setWeight(updatedPet.getWeight());
                existingPet.setRegistrationDate(updatedPet.getRegistrationDate());

                // 수정된 엔티티 저장 (낙관적 잠금 확인)
                return petRepository.save(existingPet);
            } catch (OptimisticLockingFailureException ex) {
                retryCount--;  // 재시도 횟수 감소
                if (retryCount == 0) {
                    throw new RuntimeException("Data was modified by another user, please try again.", ex);
                }
                // 일정 시간 대기 후 재시도 (예: 500ms)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // 인터럽트 처리
                }
            }
        }

        // 실패 시 예외를 던짐
        throw new RuntimeException("Failed to update pet after multiple retries.");
    }
}