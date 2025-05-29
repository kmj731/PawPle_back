package com.project.spring.skillstack.service;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    
    // 카테고리별 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        return postPage.map(PostDto::fromEntity);
    }

    // 사용 가능한 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<String> getAvailableCategories() {
        // 미리 정의된 카테고리 목록 반환
        return Arrays.asList(
            "건강토픽", "일상", "Q&A"
        );
        
        // 또는 데이터베이스에서 실제 사용중인 카테고리만 조회하려면:
        // return postRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByCategoryAndSubCategory(String category, String subCategory, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByCategoryAndSubCategoryOrderByCreatedAtDesc(category, subCategory, pageable);
        return postPage.map(PostDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSubCategories() {
        return Arrays.asList(
            "홈케어",
            "식이관리",
            "병원",
            "영양제",
            "행동",
            "질병"
        );
    }
    
    //특정 사용자의 카테고리별 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByUserAndCategory(String username, String category, int page, int size) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByUserAndCategoryOrderByCreatedAtDesc(user, category, pageable);
        return postPage.map(PostDto::fromEntity);
    }

    //카테고리 내에서 키워드 검색
    @Transactional(readOnly = true)
    public Page<PostDto> searchPostsInCategory(String keyword, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByTitleContainingOrContentContainingAndCategoryOrderByCreatedAtDesc(
            keyword, keyword, category, pageable
        );
        return postPage.map(PostDto::fromEntity);
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
        
        // 수동으로 updatedAt 설정
        post.setUpdatedAt(LocalDateTime.now());
        
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

    // 인기글 조회 (조회수 기준)
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByViews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findAllByOrderByViewCountDescCreatedAtDesc(pageable);
        return postPage.map(PostDto::fromEntity);
    }

    // 카테고리별 인기글 조회 (조회수 기준)
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByViewsInCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByCategoryOrderByViewCountDescCreatedAtDesc(category, pageable);
        return postPage.map(PostDto::fromEntity);
    }

    // 댓글 수 기준 인기글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findAllByOrderByCommentCountDescCreatedAtDesc(pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // 카테고리별 댓글 수 기준 인기글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByCommentsInCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByCategoryOrderByCommentCountDescCreatedAtDesc(category, pageable);
        return postPage.map(PostDto::fromEntity);
    }
}