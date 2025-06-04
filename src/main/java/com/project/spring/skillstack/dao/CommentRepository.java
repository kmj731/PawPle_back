package com.project.spring.skillstack.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.CommentEntity;

import jakarta.transaction.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostId(Long postId);
    List<CommentEntity> findByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CommentEntity c WHERE c.post.id = :postId")
    void deleteByPost(@Param("postId") Long postId);

    @Modifying
    @Transactional
    void deleteByUser_Id(Long userId);  // user 필드의 id 기준 삭제
}