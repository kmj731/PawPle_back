////////////////////////////////////////////// 백엔드 테스트용 컨트롤러 //////////////////////////////////////////////

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

    // 모든 유저 + 펫 정보
    @GetMapping("/test/all")
    public List<UserEntity> getAllUserEntity() {
        return userRep.findAll(); 
    }

    // 현재 로그인한 유저 + 펫 정보
    @GetMapping("/test/mypets")
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

    // 모든 게시글 조회
    @GetMapping("/test/posts")
    public List<PostEntity> getAllPosts() {
        return postRep.findAll().stream()
            .sorted(Comparator.comparing(PostEntity::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    // 현재 로그인한 유저의 게시글 조회 (페이징)
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
}
