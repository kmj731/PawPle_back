package com.project.spring.skillstack.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    
    // 모든 게시글 페이징 조회
    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 특정 사용자의 게시글 조회
    Page<PostEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    
    // 제목으로 게시글 검색
    Page<PostEntity> findByTitleContainingOrderByCreatedAtDesc(String title, Pageable pageable);
    
    // 내용으로 게시글 검색
    Page<PostEntity> findByContentContainingOrderByCreatedAtDesc(String content, Pageable pageable);
    
    // 제목 또는 내용으로 게시글 검색
    Page<PostEntity> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String title, String content, Pageable pageable);
    
    // 페이징 없이, 유저의 글 전체를 최신순으로 조회
    List<PostEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    // 조회수 증가
    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void increaseViewCount(@Param("id") Long id);
}