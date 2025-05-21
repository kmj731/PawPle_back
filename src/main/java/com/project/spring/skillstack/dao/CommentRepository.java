package com.project.spring.skillstack.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostId(Long postId);
    List<CommentEntity> findByUserId(Long userId);
}