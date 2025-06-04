package com.project.spring.skillstack.controller.permit;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.CustomUserDetails;

@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;

    @Autowired
    PetRepository petRep;

    @Autowired
    PostRepository postRep;

    // 단순 테스트
    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }

    // 모든 유저 이름 목록
    @GetMapping("/test/users")
    public List<String> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserEntity::getName)
                .collect(Collectors.toList());
    }

    // 특정 유저 엔티티 조회
    @GetMapping("/test/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserEntity> optionalUser = userRep.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "해당 ID의 사용자를 찾을 수 없습니다"));
        }

        return ResponseEntity.ok(optionalUser.get());
    }
    
    // 모든 펫 이름 목록
    @GetMapping("/test/pets")
    public List<String> getAllPets() {
        return petRep.findAll().stream()
                .map(PetEntity::getPetName)
                .collect(Collectors.toList());
    }

    // 모든 유저 엔티티 반환 (주의: 순환 참조 피하도록 엔티티에 @JsonIgnore 필요)
    @GetMapping("/test/all")
    public List<UserEntity> getAllUserEntity() {
        return userRep.findAll(); 
    }

    // 로그인한 유저의 펫 목록
    @GetMapping("/test/mypets")
    public ResponseEntity<?> getCurrentUserEntity(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "사용자 없음"));
        }

        return ResponseEntity.ok(optionalUser.get().getPets());
    }

    // 전체 게시글 목록
    @GetMapping("/test/posts")
    public List<PostEntity> getAllPosts() {
        return postRep.findAll().stream()
            .sorted(Comparator.comparing(PostEntity::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    // 로그인한 유저의 게시글 목록
    @GetMapping("/test/myposts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "사용자 없음"));
        }

        List<PostEntity> posts = postRep.findByUserOrderByCreatedAtDesc(optionalUser.get());
        return ResponseEntity.ok(posts);
    }

    // 로그인한 유저 + 게시글 목록 반환
    @GetMapping("/test/myinfo")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 정보 없음"));
        }

        Optional<UserEntity> optionalUser = userRep.findByName(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "사용자 없음"));
        }

        UserEntity user = optionalUser.get();
        List<PostEntity> posts = postRep.findByUserOrderByCreatedAtDesc(user);

        return ResponseEntity.ok(Map.of(
            "user", user,
            "posts", posts
        ));
    }
}
