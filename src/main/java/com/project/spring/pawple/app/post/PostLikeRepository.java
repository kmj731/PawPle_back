package com.project.spring.pawple.app.post;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.pawple.app.user.UserEntity;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    
    // 특정 사용자가 특정 게시글에 좋아요 했는지 확인
    boolean existsByUserAndPost(UserEntity user, PostEntity post);
    
    // 특정 사용자가 특정 게시글에 한 좋아요 찾기
    Optional<PostLikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
    
    // 특정 게시글의 좋아요 수 조회
    long countByPost(PostEntity post);
    
    // 특정 사용자가 좋아요한 게시글들 조회
    Page<PostLikeEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    
    // 게시글 삭제시 관련 좋아요 삭제
    void deleteByPost(PostEntity post);
    
    // 사용자 삭제시 관련 좋아요 삭제
    void deleteByUser(UserEntity user);
}
