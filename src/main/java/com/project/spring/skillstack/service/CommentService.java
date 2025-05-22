package com.project.spring.skillstack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.skillstack.dao.CommentRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.CommentDto;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        UserEntity user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        PostEntity post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        CommentEntity comment = CommentEntity.builder()
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .build();
        
        comment = commentRepository.save(comment);
        
        return mapToDto(comment);
    }
    
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // 권한 확인 로직 추가 필요
        if (!comment.getUser().getId().equals(commentDto.getUserId())) {
            throw new RuntimeException("You are not authorized to update this comment");
        }
        
        comment.setContent(commentDto.getContent());
        comment = commentRepository.save(comment);
        
        return mapToDto(comment);
    }
    
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // 권한 확인 로직 추가 필요
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
    
    private CommentDto mapToDto(CommentEntity comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .build();
    }
}