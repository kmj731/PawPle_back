package com.project.spring.skillstack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 게시글 생성
    @Transactional
    public PostDto createPost(PostDto postDto, String username) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        PostEntity post = postDto.toEntity();
        post.setUser(user);
        
        PostEntity savedPost = postRepository.save(post);
        return PostDto.fromEntity(savedPost);
    }
    
    // 모든 게시글 페이징 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // 게시글 상세 조회
    @Transactional
    public PostDto getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        
        // 조회수 증가
        postRepository.increaseViewCount(id);
        
        // 변경된 조회수 반영을 위해 다시 조회
        post = postRepository.findById(id).get();
        
        return PostDto.fromEntity(post);
    }
    
    // 게시글 수정
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, String username) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        
        // 작성자 검증
        if (!post.getUser().getName().equals(username)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }
        
        // 게시글 정보 업데이트
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(postDto.getCategory());
        
        PostEntity updatedPost = postRepository.save(post);
        return PostDto.fromEntity(updatedPost);
    }
    
    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, String username) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        
        // 작성자 검증
        if (!post.getUser().getName().equals(username)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }
        
        postRepository.delete(post);
    }
    
    // 특정 사용자의 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByUser(String username, int page, int size) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // 제목으로 게시글 검색
    @Transactional(readOnly = true)
    public Page<PostDto> searchPostsByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByTitleContainingOrderByCreatedAtDesc(title, pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // 내용으로 게시글 검색
    @Transactional(readOnly = true)
    public Page<PostDto> searchPostsByContent(String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByContentContainingOrderByCreatedAtDesc(content, pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // 제목 또는 내용으로 게시글 검색
    @Transactional(readOnly = true)
    public Page<PostDto> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword, keyword, pageable);
        return postPage.map(PostDto::fromEntity);
    }
}