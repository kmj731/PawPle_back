package com.project.spring.pawple.app.pet;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.spring.pawple.app.auth.CustomUserDetails;
import com.project.spring.pawple.app.health.HealthCheckRecord;
import com.project.spring.pawple.app.health.HealthCheckRecordRepository;
import com.project.spring.pawple.app.media.ImageUtil;
import com.project.spring.pawple.app.post.PostEntity;
import com.project.spring.pawple.app.post.PostRepository;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthCheckRecordRepository recordRepository;

    private PostRepository postRepository;

    @Value("${spring.security.cors.site}")
    private String corsOrigin;

    /////////////////////////// 펫 등록 ///////////////////////////
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> registerPet(
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
        String thumbnailUrl = null;

        if (image != null && !image.isEmpty()) {
            Map<String, String> urls = ImageUtil.saveImageAndThumbnail(image, "images"); // ✅ 수정
            imageUrl = urls.get("imageUrl");
            thumbnailUrl = urls.get("thumbnailUrl");
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

    /////////////////////////// 펫 수정 ///////////////////////////
    @PostMapping(value = "/update/{id}", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> updatePet(
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
            Map<String, String> urls = ImageUtil.saveImageAndThumbnail(image, "images"); // ✅ 수정
            pet.setImageUrl(urls.get("imageUrl"));
            pet.setThumbnailUrl(urls.get("thumbnailUrl"));
        }

        petRepository.save(pet);
        return ResponseEntity.ok(new PetDto(pet));
    }
    
    @PutMapping("/delete-image/{id}")
    @Transactional
    public ResponseEntity<?> deletePetImage(
        @PathVariable Long id,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<PetEntity> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "반려동물 정보 없음"));
        }

        PetEntity pet = optionalPet.get();

        if (!pet.getOwner().getName().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "수정 권한 없음"));
        }

        pet.setImageUrl(null);
        pet.setThumbnailUrl(null);
        petRepository.save(pet);

        return ResponseEntity.ok(Map.of("message", "펫 이미지 삭제 완료"));
    }

    /////////////////////////// 펫 삭제 ///////////////////////////
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<?> deletePet(
        @PathVariable Long id,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        System.out.println("[🐾 deletePet] 호출됨 - petId: " + id); // 1️⃣ 기본 로그

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "삭제 권한 없음"));
        }
        petRepository.deleteById(pet.getId());
        System.out.println("[🗑️ deletePet] 삭제 실행 완료");
        
        return ResponseEntity.ok(Map.of("message", "반려동물 삭제 완료"));
    }



    private final PetService petService;

    @GetMapping("/{petId}/age")
    public ResponseEntity<Map<String, Object>> getPetAge(@PathVariable Long petId) {
        PetDto pet = petService.getPetById(petId);
        Map<String, Object> result = new HashMap<>();
        result.put("age", pet.getPetAge());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{petId}/health-dday")
    public ResponseEntity<Map<String, Object>> getDdayByLastCheck(@PathVariable Long petId) {
        PetDto pet = petService.getPetById(petId);

        // 최신 검진 기록 가져오기
        Optional<HealthCheckRecord> lastRecord = recordRepository.findTopByPetIdOrderByCheckedAtDesc(petId);
        if (lastRecord.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "검진 기록이 없습니다."));
        }

        LocalDate checkedAt = lastRecord.get().getCheckedAt().toLocalDate();
        int age = pet.getPetAge();

        // 건강검진 날짜 계산
        LocalDate nextCheckDate = petService.calculateNextHealthCheckDateByLastCheck(checkedAt, age);

        // D-Day 메시지 생성 (dday 계산 없이 메시지만 응답)
        String ddayMessage;
        long dday = ChronoUnit.DAYS.between(LocalDate.now(), nextCheckDate);
        if (dday > 0) {
            ddayMessage = "D-" + dday;
        } else if (dday == 0) {
            ddayMessage = "D-Day";
        } else {
            ddayMessage = "D+" + Math.abs(dday);
        }

        // 응답 구성 (dday 제외)
        Map<String, Object> result = new HashMap<>();
        result.put("petName", pet.getPetName());
        result.put("age", pet.getPetAge());
        result.put("lastCheckDate", checkedAt);
        result.put("nextCheckDate", nextCheckDate);
        result.put("ddayMessage", ddayMessage);  // dday는 제거하고 ddayMessage만 보내기

        return ResponseEntity.ok(result);
    }

}
