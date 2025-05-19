package com.project.spring.skillstack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public TestAuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/token")
    public ResponseEntity<?> getTestToken() {
        String token = jwtTokenProvider.createToken("testuser", List.of("ROLE_USER"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
