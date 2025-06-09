package com.project.spring.skillstack.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.io.File;
import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dto.PostDto;
import com.project.spring.skillstack.entity.MediaEntity;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PetRepository petRepository;

    private final Random random = new Random();

    // 게시글 생성
    @Transactional
    public PostDto createPostWithMedia(PostDto postDto, List<MultipartFile> mediaFiles, MultipartFile videoFile, String username) {
        try {
            UserEntity user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PostEntity post = postDto.toEntity();
            post.setUser(user);
            post.setCreatedAt(LocalDateTime.now());

            // Pet 설정
            if (postDto.getPetId() != null) {
                PetEntity pet = petRepository.findById(postDto.getPetId())
                        .orElseThrow(() -> new RuntimeException("Pet not found"));
                post.setPet(pet);
            } else {
                List<PetEntity> pets = petRepository.findByOwner(user);
                if (!pets.isEmpty()) {
                    post.setPet(pets.get(0));
                }
            }

            // 📁 uploads 디렉토리 설정
            String baseDir = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadDir = new File(baseDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            List<MediaEntity> mediaEntities = new ArrayList<>();

            // 📷 이미지 파일 저장
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                for (MultipartFile file : mediaFiles) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        File dest = new File(baseDir, fileName);
                        file.transferTo(dest);

                        mediaEntities.add(MediaEntity.builder()
                            .fileName(fileName)
                            .fileUrl("/uploads/" + fileName)
                            .mediaType("IMAGE")
                            .post(post)
                            .build());
                    }
                }
            }

            // 🎥 영상 파일 저장
            if (videoFile != null && !videoFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
                File dest = new File(baseDir, fileName);
                videoFile.transferTo(dest);

                mediaEntities.add(MediaEntity.builder()
                    .fileName(fileName)
                    .fileUrl("/uploads/" + fileName)
                    .mediaType("VIDEO")
                    .post(post)
                    .build());
            }

            post.setMediaList(mediaEntities);
            PostEntity saved = postRepository.save(post);
            return PostDto.fromEntity(saved);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
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

    // 게시글 개수 조회
    public long getPostCount(){
        return postRepository.count();
    }

    // 좋아요 수 기준 인기글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByLikes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findAllByOrderByLikeCountDescCreatedAtDesc(pageable);
        return postPage.map(PostDto::fromEntity);
    }

    // 카테고리별 좋아요 수 기준 인기글 조회
    @Transactional(readOnly = true)
    public Page<PostDto> getPopularPostsByLikesInCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postRepository.findByCategoryOrderByLikeCountDescCreatedAtDesc(category, pageable);
        return postPage.map(PostDto::fromEntity);
    }
    
    // // 랜덤 포인트 적립 메서드
    // public boolean awardRandomPoints(String username) {
    //     // 30% 확률 체크
    //     if (random.nextInt(100) < 30) {
    //         int points = 5 + random.nextInt(11); // 5~15점 랜덤
    //         // 유저 포인트 적립 로직 예: DB 업데이트
    //         UserEntity user = userRepository.findByName(username)
    //                 .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

    //         user.setPoint(user.getPoint() + points);
    //         userRepository.save(user);

    //         return true; // 적립 완료
    //     }
    //     return false; // 적립 안됨 (확률 미충족)
    // } 

    
}