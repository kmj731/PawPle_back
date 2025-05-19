package com.project.spring.skillstack.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.CommentResponseDto;
import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.dto.PostResponseDto;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostEntity create(PostDto dto) {
        UserEntity writer = userRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostEntity post = PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(writer)
                .build();

        return postRepository.save(post);
    }

    public List<PostResponseDto> findAll() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PostResponseDto findById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToDto(post);
    }

    private PostResponseDto convertToDto(PostEntity post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter().getUsername()) // 혹은 이름 필드
                .createdAt(post.getCreatedAt())
                .comments(post.getComments().stream()
                    .map(this::convertComment)
                    .collect(Collectors.toList()))
                .build();
    }

    private CommentResponseDto convertComment(CommentEntity comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.getWriter())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}



