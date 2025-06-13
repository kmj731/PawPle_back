package com.project.spring.pawple.app.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.spring.pawple.app.auth.CookieUtil;
import com.project.spring.pawple.app.auth.JwtUtil;
import com.project.spring.pawple.app.media.ImageUtil;
import com.project.spring.pawple.app.pet.PetEntity;
import com.project.spring.pawple.app.pet.PetRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    // public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails
    // userDetails,
    // @RequestBody Map<String, String> updateData,
    // HttpServletResponse response) {
    // if (userDetails == null || userDetails.getUsername() == null) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message",
    // "로그인 정보 없음"));
    // }

    // Optional<UserEntity> optionalUser =
    // userRep.findByName(userDetails.getUsername());

    // if (optionalUser.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",
    // "사용자 없음"));
    // }

    // UserEntity user = optionalUser.get();

    // String name = updateData.get("name");
    // String password = updateData.get("pass");
    // String phone = updateData.get("phone");
    // String birthDate = updateData.get("birthDate");
    // String email = updateData.get("email");

    // if (name != null) user.setSocialName(name);
    // if (password != null && !password.isBlank())
    // user.setPass(passwordEncoder.encode(password));
    // if (phone != null) user.setPhoneNumber(phone);
    // if (birthDate != null && !birthDate.isBlank()) {
    // user.setBirthDate(LocalDate.parse(birthDate));
    // }
    // if (email != null) user.setEmail(email);

    // userRep.save(user);

    // return ResponseEntity.ok(Map.of("message", "회원정보 수정 완료"));
    // }

    // 회원정보 수정
    @PutMapping(value = "/update", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<?> updateUserWithImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") Map<String, String> updateData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletResponse response) {
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

        if (name != null)
            user.setSocialName(name);
        if (password != null && !password.isBlank())
            user.setPass(passwordEncoder.encode(password));
        if (phone != null)
            user.setPhoneNumber(phone);
        if (birthDate != null && !birthDate.isBlank()) {
            user.setBirthDate(LocalDate.parse(birthDate));
        }
        if (email != null)
            user.setEmail(email);

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

  @DeleteMapping("/withdraw")
@Transactional
public ResponseEntity<?> withdrawUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody Map<String, String> body,
        HttpServletRequest request,
        HttpServletResponse response) {

    if (userDetails == null || userDetails.getUsername() == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "로그인 정보 없음"));
    }

    String inputPassword = body.get("password");
    if (inputPassword == null || inputPassword.isBlank()) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "비밀번호를 입력해주세요."));
    }

    Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "사용자 없음"));
    }

    UserEntity user = optionalUser.get();

    if (!passwordEncoder.matches(inputPassword, user.getPass())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "비밀번호가 일치하지 않습니다."));
    }

    userRep.delete(user);

    // 인증 정보 제거
    SecurityContextHolder.clearContext();

    // ✅ JWT 쿠키 삭제 (쿠키 이름 반드시 확인!)
    Cookie jwtCookie = new Cookie("_ka_au_fo_th_", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(request.isSecure());
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0);
    response.addCookie(jwtCookie);

    return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었으며 로그아웃 되었습니다."));
}

    // 팔로우
    @PostMapping("/follow/{targetId}")
    @Transactional
    public ResponseEntity<?> followUser(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetId) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        Optional<UserEntity> targetOpt = userRep.findById(targetId);

        if (optionalUser.isEmpty() || targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "유저 정보 없음"));
        }

        UserEntity currentUser = optionalUser.get();
        UserEntity targetUser = targetOpt.get();

        if (currentUser.getId().equals(targetUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "자기 자신은 팔로우할 수 없습니다."));
        }

        // 차단 상태면 해제
        currentUser.getBlockedUsers().remove(targetUser);

        // 팔로우하지 않은 경우에만 추가
        if (!currentUser.getFollowing().contains(targetUser)) {
            currentUser.getFollowing().add(targetUser);
        }

        userRep.save(currentUser);
        return ResponseEntity.ok(Map.of("message", "팔로우 완료"));
    }

    // 언팔로우
    @DeleteMapping("/unfollow/{targetId}")
    @Transactional
    public ResponseEntity<?> unfollowUser(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetId) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        Optional<UserEntity> targetOpt = userRep.findById(targetId);

        if (optionalUser.isEmpty() || targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "유저 정보 없음"));
        }

        UserEntity currentUser = optionalUser.get();
        UserEntity targetUser = targetOpt.get();

        currentUser.getFollowing().remove(targetUser);
        userRep.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "언팔로우 완료"));
    }

    // 차단
    @PostMapping("/block/{targetId}")
    @Transactional
    public ResponseEntity<?> blockUser(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetId) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        Optional<UserEntity> targetOpt = userRep.findById(targetId);

        if (optionalUser.isEmpty() || targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "유저 정보 없음"));
        }

        UserEntity currentUser = optionalUser.get();
        UserEntity targetUser = targetOpt.get();

        if (currentUser.getId().equals(targetUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "자기 자신은 차단할 수 없습니다."));
        }

        // 팔로우 상태면 제거
        currentUser.getFollowing().remove(targetUser);

        // 차단하지 않은 경우에만 추가
        if (!currentUser.getBlockedUsers().contains(targetUser)) {
            currentUser.getBlockedUsers().add(targetUser);
        }

        userRep.save(currentUser);
        return ResponseEntity.ok(Map.of("message", "차단 완료"));
    }

    // 차단 해제
    @DeleteMapping("/unblock/{targetId}")
    @Transactional
    public ResponseEntity<?> unblockUser(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long targetId) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        Optional<UserEntity> targetOpt = userRep.findById(targetId);

        if (optionalUser.isEmpty() || targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "유저 정보 없음"));
        }

        UserEntity currentUser = optionalUser.get();
        UserEntity targetUser = targetOpt.get();

        currentUser.getBlockedUsers().remove(targetUser);
        userRep.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "차단 해제 완료"));
    }

    // 프로필 조회
    @GetMapping("/{userId}/profile")
    public UserDto getUserProfile(@PathVariable Long userId) {
        UserEntity user = userRep.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 엔티티를 DTO로 변환
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialName(user.getSocialName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthDate(user.getBirthDate());
        dto.setImageUrl(user.getImageUrl());
        dto.setThumbnailUrl(user.getThumbnailUrl());
        dto.setCreated(user.getCreated());
        dto.setPoint(user.getPoint());
        dto.setPets(user.getPets());
        dto.setBlockedIds(user.getBlockedUsers().stream().map(UserEntity::getId).collect(Collectors.toList()));
        return dto;
    }
}
