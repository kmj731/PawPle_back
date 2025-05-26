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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.utility.CookieUtil;
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
    @PutMapping("/update")
    @Transactional
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody Map<String, String> updateData,
                                        HttpServletResponse response) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();

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

        userRep.save(user);

        return ResponseEntity.ok(Map.of("message", "회원정보 수정 완료"));
    }







}
