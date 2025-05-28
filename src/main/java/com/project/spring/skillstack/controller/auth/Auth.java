package com.project.spring.skillstack.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.classes.Role;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.service.CustomUserDetails;
import com.project.spring.skillstack.utility.CookieUtil;
import com.project.spring.skillstack.utility.JwtUtil;


@RestController
@RequestMapping("/auth")
public class Auth {
    
    
    @Autowired
    UserRepository userRep;
    @Autowired
    CookieUtil cookieUtil;
    @Value("${spring.security.cors.site}")
    String corsOrigin;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;



    @GetMapping("/role")
    public Role role(@AuthenticationPrincipal CustomUserDetails user) {
        return new Role(user.getAuthorities().toArray()[0].toString().replaceFirst("ROLE_", "").toLowerCase());
    }


    // 현재 로그인한 사용자 정보 조회 API 추가
    @GetMapping("/me")
    public CustomUserDetails getCurrentUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user;
    }
}
