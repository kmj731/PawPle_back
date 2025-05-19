package com.project.spring.skillstack.controller.comment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dto.CommentDto;
import com.project.spring.skillstack.dto.CommentResponseDto;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> create(@RequestBody CommentDto dto) {
        CommentEntity comment = commentService.create(dto);
        CommentResponseDto response = CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.getWriter())
                .createdAt(comment.getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }
}

