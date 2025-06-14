package com.project.spring.pawple.app.post;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring.pawple.app.auth.CustomUserDetails;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeService likeService;

    // private final PointService pointService;

    // public PostController(PointService pointService){
    // this.pointService = pointService;
    // }
    // 게시글 총 갯수
    @GetMapping("/count")
    public ResponseEntity<Long> getPostCount() {
        long count = postService.getPostCount();
        return ResponseEntity.ok(count);
    }

    // 게시글 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart("post") String postDtoJson, // String으로 받기
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            // JSON 문자열을 PostDto로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            PostDto postDto = objectMapper.readValue(postDtoJson, PostDto.class);

            PostDto result = postService.createPostWithMedia(postDto, mediaFiles, videoFile, userDetails.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시글 생성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        // 포인트 적립 시도 : 30% 확률로 5 ~ 15점 랜덤 적립
        // try {
        // boolean awarded = pointService.awardRandomPoints(username, "게시글 작성 포인트 적립");
        // if (awarded) {
        // // 성공 시 로그 출력 또는 별도 알림 처리 가능
        // System.out.println("포인트가 적립되었습니다.");
        // }
        // } catch (Exception e) {
        // // 포인트 적립 실패해도 게시글 작성은 성공적으로 처리됨
        // System.err.println("포인트 적립 중 오류 발생: " + e.getMessage());
        // }
    }
    
    // 이미지 저장
    @PostMapping("/image-upload")
    public Map<String, String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String baseDir = System.getProperty("user.dir") + "/uploads/post/";
            File dir = new File(baseDir);
            if (!dir.exists()) dir.mkdirs();

            String uuidName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File targetFile = new File(baseDir + uuidName);
            file.transferTo(targetFile);

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", "/uploads/post/" + uuidName);
            return response;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    // 게시글 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    // 카테고리별 게시글 목록 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<PostDto>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByCategory(category, page, size);
        return ResponseEntity.ok(posts);
    }

    // 카테고리-서브카테고리별 게시글 목록 조회
    @GetMapping("/category/{category}/sub/{subCategory}")
    public ResponseEntity<Page<PostDto>> getPostsByCategoryAndSubCategory(
            @PathVariable String category,
            @PathVariable String subCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByCategoryAndSubCategory(category, subCategory, page, size);
        return ResponseEntity.ok(posts);
    }

    // 사용 가능한 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAvailableCategories() {
        List<String> categories = postService.getAvailableCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/sub")
    public ResponseEntity<List<String>> getAvailableSubCategories() {
        List<String> categories = postService.getAvailableSubCategories();
        return ResponseEntity.ok(categories);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        String username = auth.getName();

        try {
            PostDto updatedPost = postService.updatePost(id, postDto, username);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 본인 글 아님
        }
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();

        try {
            postService.deletePost(id, username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "게시글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 특정 사용자의 게시글 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByUser(username, page, size);
        return ResponseEntity.ok(posts);
    }

    // 특정 사용자의 카테고리별 게시글 조회
    @GetMapping("/user/{username}/category/{category}")
    public ResponseEntity<Page<PostDto>> getPostsByUserAndCategory(
            @PathVariable String username,
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByUserAndCategory(username, category, page, size);
        return ResponseEntity.ok(posts);
    }

    // 게시글 검색 (제목 또는 내용)
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.searchPostsInCategory(keyword, category, page, size);
        } else {
            posts = postService.searchPosts(keyword, page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 내 게시글 조회
    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostDto>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPostsByUserAndCategory(username, category, page, size);
        } else {
            posts = postService.getPostsByUser(username, page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 인기글 조회 (조회수 기준)
    @GetMapping("/popular/views")
    public ResponseEntity<Page<PostDto>> getPopularPostsByViews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPopularPostsByViewsInCategory(category, page, size);
        } else {
            posts = postService.getPopularPostsByViews(page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 댓글 수 기준 인기글 조회
    @GetMapping("/popular/comments")
    public ResponseEntity<Page<PostDto>> getPopularPostsByComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPopularPostsByCommentsInCategory(category, page, size);
        } else {
            posts = postService.getPopularPostsByComments(page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        String username = auth.getName();

        boolean isLiked = likeService.toggleLike(id, username);
        long likeCount = likeService.getLikeCount(id);

        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked,
                "likeCount", likeCount,
                "message", isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다."
        ));
    }

    // 좋아요 상태 확인
    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isLiked = likeService.isLikedByUser(id, username);
            long likeCount = likeService.getLikeCount(id);

            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 좋아요 수 기준 인기글 조회
    @GetMapping("/popular/likes")
    public ResponseEntity<Page<PostDto>> getPopularPostsByLikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category) {
        Page<PostDto> posts;
        if (category != null && !category.isEmpty()) {
            posts = postService.getPopularPostsByLikesInCategory(category, page, size);
        } else {
            posts = postService.getPopularPostsByLikes(page, size);
        }
        return ResponseEntity.ok(posts);
    }

    // 내가 좋아요한 게시글 조회
    @GetMapping("/liked")
    public ResponseEntity<Page<PostDto>> getLikedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Page<PostDto> posts = likeService.getLikedPostsByUser(username, page, size);
        return ResponseEntity.ok(posts);
    }

    // 게시글 블라인드
    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostDetail(@PathVariable Long id) {
        PostEntity post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        if (!post.getIsPublic()) {
            return ResponseEntity.ok(PostDto.blinded(post.getId()));
        }
        return ResponseEntity.ok(PostDto.fromEntity(post));
    }

    // 이전 글 조회
    @GetMapping("/{id}/previous")
    public ResponseEntity<PostDto> getPreviousPost(@PathVariable Long id) {
        PostDto previousPost = postService.findPrevious(id);
        if (previousPost == null) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(previousPost);
    }

    // 다음 글 조회
    @GetMapping("/{id}/next")
    public ResponseEntity<PostDto> getNextPost(@PathVariable Long id) {
        PostDto nextPost = postService.findNext(id);
        if (nextPost == null) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(nextPost);
    }

    // userId 기반 게시글 조회
    @GetMapping("/user/id/{userId}")
    public ResponseEntity<Page<PostDto>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/views/public")
public ResponseEntity<Page<PostDto>> getPopularPublicPostsByViews(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Page<PostDto> posts = postService.getPopularPublicPostsByViews(page, size);
    return ResponseEntity.ok(posts);
}
}