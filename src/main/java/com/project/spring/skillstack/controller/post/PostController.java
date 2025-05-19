package com.project.spring.skillstack.controller.post;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.dto.PostResponseDto;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> create(@RequestBody PostDto dto) {
        PostEntity post = postService.create(dto);
        return ResponseEntity.ok(postService.findById(post.getId()));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }
}


