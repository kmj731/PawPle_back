package com.project.spring.skillstack.controller.permit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.transaction.Transactional;



@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }






    // 모든 사용자 리스트 조회 API
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }
}
