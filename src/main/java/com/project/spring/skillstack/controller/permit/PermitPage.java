package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/permit/auth")
public class PermitPage {

    @Autowired
    UserRepository userRep;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CookieUtil cookieUtil;
    @Value("${spring.security.cors.site}")
    String corsOrigin;
    
    private final PasswordEncoder passwordEncoder;
    PermitPage(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> signup(@RequestBody UserDto dto, HttpServletResponse response) {

        if (dto.getName() == null || dto.getPass() == null || dto.getEmail() == null ||
            dto.getPhoneNumber() == null || dto.getBirthDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "empty_input"));
        }

        if (userRep.findByName(dto.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "username_exists"));
        }
        if (userRep.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "email_exists"));
        }
        if (userRep.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "phone_exists"));
        }

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        UserEntity user = new UserEntity(
                null,
                dto.getName(),
                passwordEncoder.encode(dto.getPass()), 
                dto.getSocialName() != null ? dto.getSocialName() : dto.getName(),
                roles,
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getBirthDate(),
                LocalDateTime.now(),
                null,
                null);

        userRep.save(user);

        String token = jwtUtil.generateToken(user.getName());
        cookieUtil.GenerateJWTCookie(token, response);

        return ResponseEntity.ok(Map.of("message", "success"));
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam("username") String username) {
        boolean exists = userRep.findByName(username).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/find-id")
    @ResponseBody
    public ResponseEntity<?> findId(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");

        if (name == null || email == null || phoneNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "empty_input"));
        }

        Optional<UserEntity> userOpt = userRep.findByNameAndEmailAndPhoneNumber(name, email, phoneNumber);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("username", userOpt.get().getName()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "not_found"));
        }
    }

    @PostMapping("/reset-password")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // 사용자 이름 기준
        String newPassword = request.get("newPassword");

        if (userId == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "empty_input"));
        }

        Optional<UserEntity> userOpt = userRep.findByName(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "user_not_found"));
        }

        UserEntity user = userOpt.get();
        user.setPass(passwordEncoder.encode(newPassword));
        userRep.save(user); // 변경사항 저장

        return ResponseEntity.ok(Map.of("message", "password_reset_success"));
    }


}