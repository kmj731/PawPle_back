package com.project.spring.skillstack.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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

import com.project.spring.skillstack.dto.PetDto;
import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.dto.UpdateUserRoleRequest;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.dto.UserDtoWithoutPass;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.service.PostManagerService;
import com.project.spring.skillstack.service.PostService;
import com.project.spring.skillstack.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;


// 관리자 Controller 
@RestController
@RequestMapping("/admin")

// @RequestMapping("/api")
public class ManagerController {
    
    private final UserService userService;
    private final PostService postService;
    private final PostManagerService postManagerService;

    public ManagerController(UserService userService, PostService postService, PostManagerService postManagerService){
        this.userService = userService;
        this.postService = postService;
        this.postManagerService = postManagerService;
    }

    // 전체 회원 조회
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithoutPass());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}/pets")
    public ResponseEntity<List<PetEntity>> getUserPets(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getPetsByUserId(userId));
    }

    // ✅ 이름으로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/name")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUserByName(@RequestParam String name){
        
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    // ✅ 이메일로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/email")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUsersByEmail(email));
    }

    // ✅ 소셜 이름으로 회원 검색 (관리자만)
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/social")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersBySocialName(@RequestParam String socialName) {
        return ResponseEntity.ok(userService.searchUsersBySocialName(socialName));
    }



    


    

    // 전체 게시글 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> postDto = postManagerService.getAllPost().stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(postDto);
    }


    // vet 접근 
    @PreAuthorize("hasRole('vet')")
    @GetMapping("/vet-only")
    public ResponseEntity<String> vetOnlyAccess(){
        return ResponseEntity.ok("수의사만 접근 가능한 리소스 입니다.");
    }



    // // 전체 게시글 조회 (페이징)
    // @GetMapping("/post")
    // public ResponseEntity<Page<PostDto>> getAllPosts(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(postService.getAllPosts(page, size));
    // }

    // 게시글 상세 조회
    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // 제목으로 검색
    @GetMapping(params = "title")
    public ResponseEntity<Page<PostDto>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPostsByTitle(title, page, size));
    }

    // 내용으로 검색
    @GetMapping(params = "content")
    public ResponseEntity<Page<PostDto>> searchByContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPostsByContent(content, page, size));
    }

    // 제목 또는 내용으로 검색
    @GetMapping(params = "keyword")
    public ResponseEntity<Page<PostDto>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(keyword, page, size));
    }

    // 사용자 이름으로 게시글 검색
    @GetMapping(params = "username")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByUser(username, page, size));
    }

    // // 게시글 삭제 기능
    // @DeleteMapping("/post/delete/{id}")
    // public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    //     postManagerService.deletePostById(id);
    // return ResponseEntity.noContent().build();


    // 게시글 수정 (관리자도 가능)
    @PutMapping("/post/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto dto,
            @RequestParam String username) {
        return ResponseEntity.ok(postService.updatePost(id, dto, username));
    }

    // 게시글 제목 수정 (PATCH)
    @PatchMapping("/post/update/{id}")
    public ResponseEntity<PostDto> updatePostTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        
        String newTitle = updates.get("title");
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PostEntity updatedEntity = postManagerService.updatePostTitle(id, newTitle);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }

        PostDto updatedDto = PostDto.fromEntity(updatedEntity);
        return ResponseEntity.ok(updatedDto);
    }

    // 게시글 공지로 이동
    @PatchMapping("/post/{id}/move")
    public ResponseEntity<PostDto> moveToNotice(@PathVariable Long id) {
        PostEntity updatedPost = postManagerService.setPostAsNotice(id);
        if (updatedPost == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PostDto.fromEntity(updatedPost));
    }


     // 회원 삭제 API
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
             return ResponseEntity.noContent().build(); // 204 No Content
        } else {
             return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }


    // 게시글, 회원 수 조회
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        long userCount = userService.getUserCount();
        long postCount = postService.getPostCount();

        Map<String, Long> result = new HashMap<>();
        result.put("userCount", userCount);
        result.put("postCount", postCount);

        return ResponseEntity.ok(result);
    }


    // 게시글 전체 삭제
    @DeleteMapping("/post/delete/{postId}")
    public ResponseEntity<?> deleteByPost(@PathVariable Long postId) {
        postManagerService.deletePostWithComments(postId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    // roles 변경
    @PutMapping("/user/roles")
    public ResponseEntity<?> updateUserRoles(@RequestBody UpdateUserRoleRequest request) {
        userService.updateUserRoles(request.getUserId(), request.getRoles());
        return ResponseEntity.ok().build();
    }


}
    

    
