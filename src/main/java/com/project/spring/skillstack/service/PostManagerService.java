package com.project.spring.skillstack.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dto.UpdatePostDto;
import com.project.spring.skillstack.entity.PostEntity;

@Service
public class PostManagerService {
    

    private final PostRepository postRepository;

    public PostManagerService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    // 전체 게시글 조회
    public List<PostEntity> getAllPost(){
        return postRepository.findAll();
    }

    // // 제목으로 게시글 검색
    // public List<PostEntity> searchPostByTitle(String title){
    //     return postRepository.findByTitleContainingIgnoreCase(title);
    // }

    // // 내용으로 검색
    // public List<PostEntity> searchPostByContent(String content){
    //     return postRepository.findByContentContainingIgnoreCase(content);
    // }

    // // socialName으로 검색
    // public List<PostEntity> searchPostBySocialName(String socialName){
    //     return postRepository.findBySocialNameContainingIgnoreCase(socialName);
    // }

    // // 날짜로 검색
    // public List<PostEntity> searchPostByLocalDateTime(LocalDateTime startDate, LocalDateTime endDate){
    //     return postRepository.findByCreatedBetween(startDate, endDate);
    // }

    // 게시글 상세 조회(이동)
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
    }

    // 게시글 수정
    public PostEntity updatePost(Long id, UpdatePostDto dto) {
    PostEntity post = postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }
        postRepository.deleteById(id);
    }


    // 게시글 수정
    public void updatePost(Long postId, String title, String content) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);
    }

    
}

    




    

