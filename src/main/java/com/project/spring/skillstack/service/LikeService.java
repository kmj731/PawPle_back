package com.project.spring.skillstack.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.project.spring.skillstack.dao.LikeRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.entity.LikeEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LikeService {
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 좋아요 토글 (좋아요 누르기/취소)
    @Transactional
    public boolean toggleLike(Long postId, String username) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndPost(user, post);
        
        if (existingLike.isPresent()) {
            // 이미 좋아요가 있으면 삭제 (좋아요 취소)
            likeRepository.delete(existingLike.get());
            postRepository.decreaseLikeCount(postId);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요가 없으면 추가
            LikeEntity like = LikeEntity.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
            postRepository.increaseLikeCount(postId);
            return true; // 좋아요 추가됨
        }
    }
    
    // 특정 게시글에 대한 사용자의 좋아요 상태 확인
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, String username) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        
        return likeRepository.existsByUserAndPost(user, post);
    }
    
    // 특정 게시글의 좋아요 수 조회
    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        
        return likeRepository.countByPost(post);
    }
    
    // 사용자가 좋아요한 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getLikedPostsByUser(String username, int page, int size) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LikeEntity> likePage = likeRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return likePage.map(like -> PostDto.fromEntity(like.getPost()));
    }
}