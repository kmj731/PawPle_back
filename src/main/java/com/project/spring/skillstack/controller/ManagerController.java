package com.project.spring.skillstack.controller;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.UpdatePostDto;
import com.project.spring.skillstack.dto.UpdateUserDto;
import com.project.spring.skillstack.dto.UserDtoWithoutPass;
import com.project.spring.skillstack.entity.PostEntity;

import com.project.spring.skillstack.service.PostService;
import com.project.spring.skillstack.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


// 관리자 Controller 
@RestController
// @RequestMapping("/admin")
@RequestMapping("/api")
public class ManagerController {
    
    private final UserService userService;
    private final PostService postService;

    public ManagerController(UserService userService, PostService postService){
        this.userService = userService;
        this.postService = postService;
    }

    // 전체 회원 조회
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithoutPass());
    }



    // ✅ 이름으로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUserByName(@RequestParam String name){
        
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    // ✅ 이메일로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUsersByEmail(email));
    }

    // ✅ 소셜 이름으로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersBySocialName(@RequestParam String socialName) {
        return ResponseEntity.ok(userService.searchUsersBySocialName(socialName));
    }

    // // 회원 정보 수정 (관리자만)
    // // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/users")
    // public ResponseEntity<UserDtoWithoutPass> updateUser(
    //     @PathVariable Long id,
    //     @RequestBody UpdateUserDto dto
    // ){
    //     return ResponseEntity.ok(userService.updateUser(id,dto));
    // }

    // // 이름 수정
    // @PatchMapping("/name")
    // public ResponseEntity<?> updateName(@RequestBody NameUpdateRequest request) {
    //     userService.updateName(request.getUserId(), request.getName());
    //     return ResponseEntity.ok("Name updated successfully");
    // }

    // // 소셜 이름 수정
    // @PatchMapping("/socialName")
    // public ResponseEntity<?> updateSocialName(@RequestBody SocialNameUpdateRequest request) {
    //     userService.updateSocialName(request.getUserId(), request.getSocialName());
    //     return ResponseEntity.ok("Social Name updated successfully");
    // }

    // // 이메일 수정
    // @PatchMapping("/email")
    // public ResponseEntity<?> updateEmail(@RequestBody EmailUpdateRequest request) {
    //     userService.updateEmail(request.getUserId(), request.getEmail());
    //     return ResponseEntity.ok("Email updated successfully");
    // }

    // // 전화번호 수정
    // @PatchMapping("/phoneNumber")
    // public ResponseEntity<?> updatePhoneNumber(@RequestBody PhoneNumberUpdateRequest request) {
    //     userService.updatePhoneNumber(request.getUserId(), request.getPhoneNumber());
    //     return ResponseEntity.ok("Phone Number updated successfully");
    // }

    
    // 회원 삭제
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // // 전체 게시글 조회
    // // @PreAuthorize("hasRole('ADMIN')")
    // @GetMapping("/post")
    // public ResponseEntity<List<PostEntity>> getAllPosts(){
    //     List<PostEntity> posts = postService.getAllPost();
    //     return ResponseEntity.ok(posts);
    // }

    // // 제목으로 게시글 조회
    // // @PreAuthorize("hasRole('ADMIN')")
    // @GetMapping("/post")
    // public ResponseEntity<List<PostEntity>> searchPostsByTitle(@RequestParam String title){
    //     List<PostEntity> posts = postService.searchPostByTitle(title);
    //     return ResponseEntity.ok(posts);
    // }

    // // 내용으로 게시글 조회
    // // @PreAuthorize("hasRole('ADMIN')")
    // @GetMapping("/post")
    // public ResponseEntity<List<PostEntity>> searchPostsByContent(@RequestParam String content) {
    //     List<PostEntity> posts = postService.searchPostByContent(content);
    //     return ResponseEntity.ok(posts);
    // }

    //  // 소셜 이름으로 게시글 조회
    // @GetMapping("/post")
    // public ResponseEntity<List<PostEntity>> searchPostsBySocialName(@RequestParam String socialName) {
    //     List<PostEntity> posts = postService.searchPostBySocialName(socialName);
    //     return ResponseEntity.ok(posts);
    // }

    // // 날짜 범위로 게시글 조회
    // @GetMapping("/post")
    // public ResponseEntity<List<PostEntity>> searchPostByLocalDateTime(@RequestParam String startDate, @RequestParam String endDate) {
    //     // String -> LocalDateTime 변환 필요
    //     LocalDateTime start = LocalDateTime.parse(startDate);
    //     LocalDateTime end = LocalDateTime.parse(endDate);

    //     List<PostEntity> posts = postService.searchPostByLocalDateTime(start, end);
    //     return ResponseEntity.ok(posts);
    // }
    

    // // 게시글 상세 조회
    // @GetMapping("/post")
    // public ResponseEntity<PostEntity> getPostById(@PathVariable Long id) {
    //     PostEntity post = postService.getPostById(id);
    //     return ResponseEntity.ok(post);
    // }

    // // 게시글 수정
    // @PutMapping("/post")
    // public ResponseEntity<PostEntity> updatePost(@PathVariable Long id, @RequestBody UpdatePostDto dto){
    //     PostEntity updated = postService.updatePost(id, dto);
    //     return ResponseEntity.ok(updated);
    // }
    
    // // 게시글 삭제
    // @DeleteMapping("/post")
    // public ResponseEntity<Void> deletePost(@PathVariable Long id){
    //     postService.deletePost(id);
    //     return ResponseEntity.noContent().build();
    // }

    // DOCTOR 접근 
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor-only")
    public ResponseEntity<String> doctorOnlyAccess(){
        return ResponseEntity.ok("의사만 접근 가능한 리소스 입니다.");
    }

}
    

