package com.project.spring.skillstack.controller.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;
    
    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        PostDto createdPost = postService.createPost(postDto, username);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    
    // 게시글 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }
    
    // 인기글 조회 (조회수 기준)
    @GetMapping("/popular/views")
    public ResponseEntity<Page<PostDto>> getPopularPostsByViews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPopularPostsByViewsInCategory(category, page, size);
        } else {
            posts = postService.getPopularPostsByViews(page, size);
        }
        return ResponseEntity.ok(posts);
    }
    
    // 인기글 조회 (댓글수 기준) - 댓글 기능이 있다면
    @GetMapping("/popular/comments")
    public ResponseEntity<Page<PostDto>> getPopularPostsByComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPopularPostsByCommentsInCategory(category, page, size);
        } else {
            posts = postService.getPopularPostsByComments(page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 카테고리별 게시글 목록 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<PostDto>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByCategory(category, page, size);
        return ResponseEntity.ok(posts);
    }

    // 사용 가능한 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAvailableCategories() {
        List<String> categories = postService.getAvailableCategories();
        return ResponseEntity.ok(categories);
    }   

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            PostDto updatedPost = postService.updatePost(id, postDto, username);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            postService.deletePost(id, username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "게시글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // 특정 사용자의 게시글 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByUser(username, page, size);
        return ResponseEntity.ok(posts);
    }

    // 특정 사용자의 카테고리별 게시글 조회
    @GetMapping("/user/{username}/category/{category}")
    public ResponseEntity<Page<PostDto>> getPostsByUserAndCategory(
            @PathVariable String username,
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByUserAndCategory(username, category, page, size);
        return ResponseEntity.ok(posts);
    }
    
    // 게시글 검색 (제목 또는 내용)
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.searchPostsInCategory(keyword, category, page, size);
        } else {
            posts = postService.searchPosts(keyword, page, size);
        }
        return ResponseEntity.ok(posts);
    }
    
    // 내 게시글 조회
    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostDto>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, 
            @RequestParam(required = false) String category) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPostsByUserAndCategory(username, category, page, size);
        } else {
            posts = postService.getPostsByUser(username, page, size);
        }
        return ResponseEntity.ok(posts);
    }
}