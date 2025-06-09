package com.project.spring.skillstack.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.CommentLikeEntity;
import com.project.spring.skillstack.entity.UserEntity;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    
    Optional<CommentLikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);

    boolean existsByUserAndComment(UserEntity user, CommentEntity comment);

    void deleteByUserAndComment(UserEntity user, CommentEntity comment);

    long countByComment(CommentEntity comment);
}


