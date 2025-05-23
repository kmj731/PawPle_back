package com.project.spring.skillstack.controller.auth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // 회원삭제
    @PostMapping("/delete")
    @Transactional
    public String deleteUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/mypage?error=wrong_password";
        }

        try {
            Optional<UserEntity> optionalUser = userRep.findByName(loggedInUsername);
            if (optionalUser.isEmpty()) {
                return "redirect:/auth/signin?error=user_not_found";
            }

            UserEntity user = optionalUser.get();

            List<PetEntity> pets = petRep.findByOwner(user);
            if (!pets.isEmpty()) {
                petRep.deleteAll(pets);
            }

            userRep.delete(user);
            SecurityContextHolder.clearContext();
            cookieUtil.RemoveJWTCookie(response);

            return "redirect:" + corsOrigin + "/home?message=delete_success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + corsOrigin + "/mypage?error=delete_failed";
        }

    }
    

    // 정보수정
    @PostMapping("/update")
    @Transactional
    public String updateUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("password1") String password1,
        @RequestParam("password2") String password2,
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("phoneNumber") String phoneNumber,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            return "redirect:" + corsOrigin + "/signin?error=not_logged_in";
        }

        String loggedInUsername = userDetails.getUsername();

        if (!passwordEncoder.matches(password1, userDetails.getPassword())) {
            return "redirect:" + corsOrigin + "/mypage?error=wrong_password";
        }
        

        Optional<UserEntity> userOptional = userRep.findByName(loggedInUsername);
        if (userOptional.isEmpty()) {
            return "redirect:" + corsOrigin + "signin?error=user_not_found";
        }

        UserEntity user = userOptional.get();

        if (password2 != null && !password2.isEmpty()) {
            user.setPass(passwordEncoder.encode(password2));
            
            String newToken = jwtUtil.generateToken(user.getName());
            cookieUtil.GenerateJWTCookie(newToken, response);
        }

        if (name != null && !name.isEmpty()) {
            user.setSocialName(name);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }

        userRep.save(user);

        return "redirect:" + corsOrigin + "/mypage?message=update_success";
    }

}
