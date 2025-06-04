package com.project.spring.skillstack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.spring.skillstack.dao.CommentLikeRepository;
import com.project.spring.skillstack.dao.CommentRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.CommentDto;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.CommentLikeEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    // private final CommentLikeRepository commentLikeRepository;
    private final CommentLikeService commentLikeService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        UserEntity user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        PostEntity post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        CommentEntity parent = null;
        if (commentDto.getParentId() != null) {
            parent = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        CommentEntity comment = CommentEntity.builder()
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .parent(parent)
                .build();

        // 포인트 적립
        user.addPoint(1);
        userRepository.save(user);
        
        comment = commentRepository.save(comment);
        
        // 게시글의 댓글 수 증가
        postRepository.increaseCommentCount(commentDto.getPostId());

        return mapToDto(comment);
    }
    
    // 전체 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
        
        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        
        // 게시글의 댓글 수 감소
        postRepository.decreaseCommentCount(postId);
    }


    public CommentDto mapToDto(CommentEntity comment) {
        List<CommentDto> childDtos = comment.getChildren() != null ?
                comment.getChildren().stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
                : new ArrayList<>();

        long likeCount = commentLikeService.getLikeCount(comment.getId());

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .children(childDtos)
                .likeCount(likeCount)
                .build();
    }
}