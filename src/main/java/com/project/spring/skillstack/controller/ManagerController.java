package com.project.spring.skillstack.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.dto.UpdatePostDto;
import com.project.spring.skillstack.dto.UpdateUserDto;
import com.project.spring.skillstack.dto.UserDto;
import com.project.spring.skillstack.dto.UserDtoWithoutPass;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.service.PostManagerService;
import com.project.spring.skillstack.service.PostService;
import com.project.spring.skillstack.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


// 관리자 Controller 
@RestController
<<<<<<< Updated upstream
// @RequestMapping("/admin")
<<<<<<< Updated upstream
@RequestMapping("/management")
=======
@RequestMapping("/admin")
>>>>>>> Stashed changes
=======
@RequestMapping("/admin")
// @RequestMapping("/management")
>>>>>>> Stashed changes
public class ManagerController {

    private final PostManagerService postManagerService;
    
    private final UserService userService;
    private final PostService postService;

    public ManagerController(UserService userService, PostService postService, PostManagerService postManagerService){
        this.userService = userService;
        this.postService = postService;
        this.postManagerService = postManagerService;
    }

    // 전체 회원 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithoutPass());
    }



    // ✅ 이름으로 회원 검색 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/name")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUserByName(@RequestParam String name){
        
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    // ✅ 이메일로 회원 검색 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/email")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUsersByEmail(email));
    }

    // ✅ 소셜 이름으로 회원 검색 (관리자만)
<<<<<<< Updated upstream
    @PreAuthorize("hasRole('ADMIN')")
=======
    // @PreAuthorize("hasRole('ADMIN')")
>>>>>>> Stashed changes
    @GetMapping("/user/social")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersBySocialName(@RequestParam String socialName) {
        return ResponseEntity.ok(userService.searchUsersBySocialName(socialName));
    }

    // 게시글 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/posts/update")
    public ResponseEntity<?> updatePost(
        @RequestBody PostDto postDto,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String name = userDetails.getUsername();

        UserEntity user = userService.findByName(name)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        postManagerService.updatePost(postDto.getTitle(), user, postDto, user.getName());

        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }


    // 유저 삭제
    @DeleteMapping("/userDel{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String name,@AuthenticationPrincipal UserDetails userDetails){
        userService.deleteUser(name, userDetails.getUsername());
        return ResponseEntity.ok("회원이 성공적으로 탈퇴되었습니다.");
    }

    // 회원 삭제(관리자만)


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

    
    // // 회원 삭제
    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("/user/delete")
    // public ResponseEntity<Void> deleteUser(@PathVariable Long id){
    //     userService.deleteUser(id);
    //     return ResponseEntity.noContent().build();
    // }

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



    // 전체 게시글 조회 (페이징)
    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    // 게시글 상세 조회
<<<<<<< Updated upstream
    @GetMapping("/posts/{id}")
=======
    @GetMapping("/post/{id}")
>>>>>>> Stashed changes
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

    // 게시글 삭제 (관리자도 가능)
<<<<<<< Updated upstream
    @DeleteMapping("/posts/delete/{id}")
=======
    @DeleteMapping("/delete/{id}")
>>>>>>> Stashed changes
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam String username) {
        postService.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }

<<<<<<< Updated upstream
    @PutMapping("/posts/update/{id}")
=======
    // 게시글 수정 (관리자도 가능)
    @PutMapping("/update/{id}")
>>>>>>> Stashed changes
    public ResponseEntity<PostDto> updatePost(
        @PathVariable Long id,
        @RequestBody PostDto postDto,
        @AuthenticationPrincipal UserDetails userDetails) {
    
        String username = userDetails.getUsername();
        PostDto updatedPostDto = postService.updatePost(id, postDto, username);
    
        return ResponseEntity.ok(updatedPostDto);
    }

    // 의사 역할 변경
    @PutMapping("/posts/{id}/role/doctor")
    public ResponseEntity<?> promoteToDoctor(@PathVariable String name, @AuthenticationPrincipal UserDetails userDetails ){
        userService.updateRoleToDoctor(name, userDetails.getUsername());
        return ResponseEntity.ok("사용자의 역할이 Doctor로 변경되었습니다.");
    }

}
    

