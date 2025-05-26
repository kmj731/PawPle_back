package com.project.spring.skillstack.controller.auth;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.PetDto;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.security.cors.site}")
    private String corsOrigin;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerPet(
            @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "사용자 없음"));
        }

        UserEntity owner = optionalUser.get();

        Optional<PetEntity> existingPet = petRepository.findByOwnerAndPetName(owner, dto.getPetName());
        if (existingPet.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 등록된 반려동물 이름입니다."));
        }

        PetEntity pet = new PetEntity(
                dto.getPetType(),
                dto.getWeight(),
                dto.getPetName(),
                dto.getPetAge(),
                dto.getPetGender(),
                dto.getPetBreed(),
                LocalDate.now(),
                owner
        );

        PetEntity savedPet = petRepository.save(pet);
        PetDto petDto = new PetDto(savedPet);

        return ResponseEntity.ok(petDto);
    }


    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<?> updatePet(
            @PathVariable Long id,
            @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "사용자 없음"));
        }

        Optional<PetEntity> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "반려동물 정보 없음"));
        }

        PetEntity pet = optionalPet.get();

        // 소유자 검증
        if (!pet.getOwner().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "수정 권한 없음"));
        }

        pet.setPetType(dto.getPetType());
        pet.setWeight(dto.getWeight());
        pet.setPetName(dto.getPetName());
        pet.setPetAge(dto.getPetAge());
        pet.setPetGender(dto.getPetGender());
        pet.setPetBreed(dto.getPetBreed());

        petRepository.save(pet);

        return ResponseEntity.ok(new PetDto(pet));
    }

}
