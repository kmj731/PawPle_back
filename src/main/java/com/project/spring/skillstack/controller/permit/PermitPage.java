package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
                new BCryptPasswordEncoder().encode(dto.getPass()),
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

    private final PasswordEncoder passwordEncoder;
    PermitPage(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/signin")
    @ResponseBody
    public ResponseEntity<?> signin(@RequestParam("userId") String userId,
                                    @RequestParam("password") String password,
                                    HttpServletResponse response) {

        UserEntity user = userRep.findByName(userId).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPass())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 실패"));
        }

        String token = jwtUtil.generateToken(userId);
        
        // 선택: 쿠키에도 저장
        cookieUtil.GenerateJWTCookie(token, response);

        // 본문에 JSON으로 토큰을 응답
        return ResponseEntity.ok(Map.of("token", token));
    }
    // public ResponseEntity<Map<String, String>> signin(
    //         @RequestBody Map<String, String> credentials,
    //         HttpServletResponse response) {

    //     String userId = credentials.get("userId");
    //     String password = credentials.get("password");

    //     UserEntity user = userRep.findByName(userId).orElse(null);

    //     if (user == null) {
    //         Map<String, String> responseMap = new HashMap<>();
    //         responseMap.put("error", "not_found");
    //         return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    //     }

    //     BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    //     if (!encoder.matches(password, user.getPass())) {
    //         Map<String, String> responseMap = new HashMap<>();
    //         responseMap.put("error", "wrong_password");
    //         return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    //     }

    //     String token = jwtUtil.generateToken(user.getName());
    //     cookieUtil.GenerateJWTCookie(token, response);

    //     Map<String, String> successResponse = new HashMap<>();
    //     successResponse.put("redirect", corsOrigin + "/home");
    //     return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    // }

    @PostMapping("/findid")
    @ResponseBody
    public String findUserId(
            @RequestParam("email1") String email1,
            @RequestParam("email2") String email2,
            @RequestParam("phoneNumber2") String phoneNumber2,
            @RequestParam("phoneNumber3") String phoneNumber3) {
        String email = email1 + "@" + email2;
        String phoneNumber = "010" + phoneNumber2 + phoneNumber3;

        Optional<UserEntity> optUser = userRep.findByEmail(email)
                .filter(user -> phoneNumber.equals(user.getPhoneNumber()));

        return optUser.map(UserEntity::getName)
                .orElse("일치하는 회원 정보가 없습니다.");
    }

    @PostMapping("/findpw")
    public String findpw(
            @RequestParam("userId") String userId,
            @RequestParam("email1") String email1,
            @RequestParam("email2") String email2,
            @RequestParam("phone2") String phone2,
            @RequestParam("phone3") String phone3,
            Model model) {
        String email = email1 + "@" + email2;
        String phone = "010" + phone2 + phone3;

        Optional<UserEntity> opt = userRep.findByName(userId)
                .filter(u -> email.equals(u.getEmail()) && phone.equals(u.getPhoneNumber()));

        if (opt.isPresent()) {
            model.addAttribute("userId", userId);
            return "redirect:" + corsOrigin + "/resetpw";
        } else {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
            return "redirect:" + corsOrigin + "/findpw";
        }
    }

    @PostMapping("/resetpw")
    @Transactional
    public String resetpw(
            @RequestParam("userId") String userId,
            @RequestParam("newPassword1") String pw1,
            @RequestParam("newPassword2") String pw2,
            Model model) {
        if (!pw1.equals(pw2)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            return "redirect:" + corsOrigin + "/resetpw";
        }

        Optional<UserEntity> opt = userRep.findByName(userId);
        if (opt.isPresent()) {
            UserEntity user = opt.get();
            user.setPass(new BCryptPasswordEncoder().encode(pw1));
            userRep.save(user);
            return "redirect:" + corsOrigin + "/signin?reset=success";
        } else {
            return "redirect:" + corsOrigin + "/findpw?error=유저를 찾을 수 없습니다.";
        }
    }

}