package com.project.spring.skillstack.service;

import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.dao.PostRepository;  
import com.project.spring.skillstack.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void createPost(PostDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); // 변경됨

        UserEntity user = userRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("User not found")); // 변경됨

        PostEntity post = PostEntity.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .user(user)
            .build();

        postRepository.save(post);
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream().map(post -> PostDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .username(post.getUser().getName()) // 변경됨
            .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            .build()).collect(Collectors.toList());
    }

    public PostDto getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .username(post.getUser().getName()) // 변경됨
            .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            .build();
    }
}
