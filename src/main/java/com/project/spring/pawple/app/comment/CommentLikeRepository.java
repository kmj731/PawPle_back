package com.project.spring.pawple.app.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.pawple.app.user.UserEntity;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    
    Optional<CommentLikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);

    boolean existsByUserAndComment(UserEntity user, CommentEntity comment);

    void deleteByUserAndComment(UserEntity user, CommentEntity comment);

    long countByComment(CommentEntity comment);
}


