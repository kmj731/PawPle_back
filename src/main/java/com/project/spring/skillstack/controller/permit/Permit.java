////////////////////////////////////////////// 백엔드 테스트용 컨트롤러 //////////////////////////////////////////////

package com.project.spring.skillstack.controller.permit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.CustomUserDetails;




@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;
    @Autowired
    PetRepository petRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }







    // 모든 유저 리스트 조회 API (이름만)
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }

    // 모든 펫 리스트 조회 API (이름만)
    @GetMapping("/test/pets")
    public List<String> getAllPets() {
        return petRep.findAll().stream()
                .map(PetEntity::getPetName)
                .collect(Collectors.toList());
    }

    // 현재 로그인한 유저 + 펫 정보
    @GetMapping("/test/userpet")
    public ResponseEntity<?> getCurrentUserEntity(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "사용자 없음"));
        }

        return ResponseEntity.ok(optionalUser.get()); 
    }

    // 모든 유저 + 펫 정보
    @GetMapping("/test/all")
    public List<UserEntity> getAllUserEntity() {
        return userRep.findAll(); 
    }

}
