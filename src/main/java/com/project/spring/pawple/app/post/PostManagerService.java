package com.project.spring.pawple.app.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.project.spring.pawple.app.user.Role;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PostManagerService {
    
    @Autowired
    private UserRepository userRepository;

    private final PostRepository postRepository;

    public PostManagerService(UserRepository userRepository,PostRepository postRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        
    }

    // 전체 게시글 조회
    public List<PostEntity> getAllPost(){
        return postRepository.findAll();
    }


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

    public void deletePostById(Long id){
        postRepository.deleteById(id);
    }


    @Transactional
    public void updatePost(String title, UserEntity user, PostDto postDto, String username) {
        PostEntity post = postRepository.findByTitleAndUser(title, user)
            .orElseThrow(() -> new RuntimeException("해당 사용자의 게시글을 찾을 수 없습니다."));
    
        // 작성자가 아니고 관리자도 아닐 경우 권한 없음
        if (!post.getUser().getName().equals(username) && !isAdmin(username)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
    
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCategory(postDto.getCategory());
        post.setIsPublic(postDto.getIsPublic());
        post.setUpdatedAt(LocalDateTime.now());
    
        postRepository.save(post); // 선택사항: save 생략해도 JPA가 영속성 컨텍스트에서 자동 반영
    }

    private boolean isAdmin(String username) {
        UserEntity user = userRepository.findByName(username)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return user.getRoles() != null && user.getRoles().contains("ADMIN");
    }

    // 제목만 수정
    @Transactional
    public PostEntity updatePostTitle(Long postId, String newTitle) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
        PostEntity post = optionalPost.get();
        post.setTitle(newTitle);
        return postRepository.save(post);
    }

    // 공지로 이동
    @Transactional
    public PostEntity setPostAsNotice(Long postId) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
        PostEntity post = optionalPost.get();
        
        // 방법1: category 필드를 "공지"로 변경
        post.setCategory("공지");
        
        // 방법2: 만약 isNotice Boolean 필드가 있다면
        // post.setIsNotice(true);

        return postRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePostWithComments(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        
        postRepository.delete(post); // 댓글도 자동 삭제됨 (Cascade 설정에 의해)
    }



    public List<PostEntity> searchByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<PostEntity> searchByUser(String username) {
        return postRepository.findByUser_NameContainingIgnoreCase(username);
    }


      // 게시글 이동
    public PostEntity movePostCategory(Long postId, String category, String subCategory) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
    
        PostEntity post = optionalPost.get();
        post.setCategory(category);
        post.setSubCategory(subCategory); // null이면 null로 설정
        return postRepository.save(post);
    }

    
}





    
    
    


    




    

