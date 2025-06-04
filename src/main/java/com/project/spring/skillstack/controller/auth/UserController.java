package com.project.spring.skillstack.controller.auth;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.ImageUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/user")
public class UserController {
    
    
    @Autowired
    UserRepository userRep;
    @Autowired
    PetRepository petRep;
    @Autowired
    CookieUtil cookieUtil;
    @Value("${spring.security.cors.site}")
    String corsOrigin;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @Value("${spring.security.jwt.cookie.name}")
    String jwtCookieName;




    // 회원정보 조회
    @ResponseBody
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserDto userDto = optionalUser.get().toDto();
        return ResponseEntity.ok(userDto);
    }

    // 펫정보 조회
    @ResponseBody
    @GetMapping("/petinfo")
    public ResponseEntity<?> getMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();
        List<PetEntity> pets = petRep.findByOwner(user);
        return ResponseEntity.ok(pets);
    }

    // 비밀번호 확인
    @PostMapping("/checkpw")
    public ResponseEntity<?> checkPassword(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody Map<String, String> body) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        String inputPassword = body.get("password");
        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();

        if (!passwordEncoder.matches(inputPassword, user.getPass())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "비밀번호가 일치하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of("message", "비밀번호 확인 성공"));
    }

    // 회원정보 수정
    // @PutMapping("/update")
    // @Transactional
    // public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
    //                                     @RequestBody Map<String, String> updateData,
    //                                     HttpServletResponse response) {
    //     if (userDetails == null || userDetails.getUsername() == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
    //     }

    //     Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());

    //     if (optionalUser.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
    //     }

    //     UserEntity user = optionalUser.get();

    //     String name = updateData.get("name");
    //     String password = updateData.get("pass");
    //     String phone = updateData.get("phone");
    //     String birthDate = updateData.get("birthDate");
    //     String email = updateData.get("email");
        
    //     if (name != null) user.setSocialName(name);
    //     if (password != null && !password.isBlank()) user.setPass(passwordEncoder.encode(password));
    //     if (phone != null) user.setPhoneNumber(phone);
    //     if (birthDate != null && !birthDate.isBlank()) {
    //         user.setBirthDate(LocalDate.parse(birthDate));
    //     }
    //     if (email != null) user.setEmail(email);

    //     userRep.save(user);

    //     return ResponseEntity.ok(Map.of("message", "회원정보 수정 완료"));
    // }

    @PutMapping(value = "/update", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> updateUserWithImage(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestPart("data") Map<String, String> updateData,
        @RequestPart(value = "image", required = false) MultipartFile image,
        HttpServletResponse response
    ) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();

        // 기존 업데이트 처리
        String name = updateData.get("name");
        String password = updateData.get("pass");
        String phone = updateData.get("phone");
        String birthDate = updateData.get("birthDate");
        String email = updateData.get("email");
        
        if (name != null) user.setSocialName(name);
        if (password != null && !password.isBlank()) user.setPass(passwordEncoder.encode(password));
        if (phone != null) user.setPhoneNumber(phone);
        if (birthDate != null && !birthDate.isBlank()) {
            user.setBirthDate(LocalDate.parse(birthDate));
        }
        if (email != null) user.setEmail(email);

        // ✅ 이미지 저장
        if (image != null && !image.isEmpty()) {
            Map<String, String> urls = ImageUtil.saveImageAndThumbnail(image, "images");
            user.setImageUrl(urls.get("imageUrl"));
            user.setThumbnailUrl(urls.get("thumbnailUrl"));
        }

        userRep.save(user);
        return ResponseEntity.ok(Map.of("message", "회원정보 수정 완료"));
    }

    // 이미지 삭제
    @DeleteMapping("/delete-image")
    @Transactional
    public ResponseEntity<?> deleteProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();
        user.setImageUrl(null);
        user.setThumbnailUrl(null);

        userRep.save(user); // DB 업데이트 반영

        return ResponseEntity.ok(Map.of("message", "이미지 삭제 완료"));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    @Transactional
    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();

        // 연관된 PetEntity들도 함께 제거됨 (orphanRemoval = true 설정 덕분에)
        userRep.delete(user);

        // 쿠키 제거 (선택: 로그아웃 처리 유도)
        // 실제 환경에 맞춰 response에서 쿠키 삭제하거나 리다이렉트 처리 가능
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }



}
