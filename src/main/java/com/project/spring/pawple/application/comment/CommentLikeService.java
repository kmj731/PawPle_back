package com.project.spring.pawple.application.comment;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.application.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public boolean toggleLike(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository.findByUserAndComment(user, comment);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            commentLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // 좋아요 추가
            CommentLikeEntity like = CommentLikeEntity.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(like);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));
        return commentLikeRepository.existsByUserAndComment(user, comment);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));
        return commentLikeRepository.countByComment(comment);
    }
}

