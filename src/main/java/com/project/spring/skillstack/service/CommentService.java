package com.project.spring.skillstack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.CommentRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dto.CommentDto;
import com.project.spring.skillstack.dto.CommentResponseDto;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.PostEntity;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentEntity create(CommentDto dto) {
        PostEntity post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommentEntity comment = CommentEntity.builder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .post(post)
                .build();

        return commentRepository.save(comment);
    }

    public List<CommentResponseDto> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .writer(comment.getWriter())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}


