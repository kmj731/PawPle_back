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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.security.cors.site}")
    private String corsOrigin;

    /**
     * 일반 등록 (JSON만 전송)
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerPet(
            @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity owner = optionalUser.get();

        Optional<PetEntity> existingPet = petRepository.findByOwnerAndPetName(owner, dto.getPetName());
        if (existingPet.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 등록된 반려동물 이름입니다."));
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
        return ResponseEntity.ok(new PetDto(savedPet));
    }

    /**
     * 수정 (JSON만 전송)
     */
    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<?> updatePet(
            @PathVariable Long id,
            @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        Optional<PetEntity> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "반려동물 정보 없음"));
        }

        PetEntity pet = optionalPet.get();

        if (!pet.getOwner().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "수정 권한 없음"));
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

    /**
     * 이미지 포함 등록
     */
    @PostMapping(value = "/register-with-image", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> registerPetWithImage(
        @RequestPart("data") PetDto dto,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity owner = optionalUser.get();

        Optional<PetEntity> existingPet = petRepository.findByOwnerAndPetName(owner, dto.getPetName());
        if (existingPet.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 등록된 반려동물 이름입니다."));
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = saveImage(image);
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
        pet.setImageUrl(imageUrl);

        PetEntity savedPet = petRepository.save(pet);
        return ResponseEntity.ok(new PetDto(savedPet));
    }

    /**
     * 이미지 포함 수정
     */
    @PostMapping(value = "/update-with-image/{id}", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> updatePetWithImage(
        @PathVariable Long id,
        @RequestPart("data") PetDto dto,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRepository.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        Optional<PetEntity> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "반려동물 정보 없음"));
        }

        PetEntity pet = optionalPet.get();

        if (!pet.getOwner().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "수정 권한 없음"));
        }

        pet.setPetType(dto.getPetType());
        pet.setWeight(dto.getWeight());
        pet.setPetName(dto.getPetName());
        pet.setPetAge(dto.getPetAge());
        pet.setPetGender(dto.getPetGender());
        pet.setPetBreed(dto.getPetBreed());

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            pet.setImageUrl(imageUrl);
        }

        petRepository.save(pet);
        return ResponseEntity.ok(new PetDto(pet));
    }

    /**
     * 이미지 저장 메서드
     */
    private String saveImage(MultipartFile file) {
        try {
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            String folder = System.getProperty("user.dir") + "/uploads/images/"; // ✅ 절대경로
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(folder + filename);

            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            return "/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
