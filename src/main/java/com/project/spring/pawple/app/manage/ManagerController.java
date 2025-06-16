package com.project.spring.pawple.app.manage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.project.spring.pawple.app.order.OrderRepository;
import com.project.spring.pawple.app.pet.PetDto;
import com.project.spring.pawple.app.pet.PetEntity;
import com.project.spring.pawple.app.pet.PetShowing;
import com.project.spring.pawple.app.pet.PetUserDto;
import com.project.spring.pawple.app.post.PostDto;
import com.project.spring.pawple.app.post.PostEntity;
import com.project.spring.pawple.app.post.PostManagerService;
import com.project.spring.pawple.app.post.PostRepository;
import com.project.spring.pawple.app.post.PostService;
import com.project.spring.pawple.app.report.ReportDto;
import com.project.spring.pawple.app.report.ReportEntity;
import com.project.spring.pawple.app.report.ReportRepository;
import com.project.spring.pawple.app.report.ReportService;
import com.project.spring.pawple.app.review.ReviewDto;
import com.project.spring.pawple.app.review.ReviewEntity;
import com.project.spring.pawple.app.review.ReviewRepository;
import com.project.spring.pawple.app.review.ReviewService;
import com.project.spring.pawple.app.store.ProductEntity;
import com.project.spring.pawple.app.store.ProductService;
import com.project.spring.pawple.app.user.UserDto;
import com.project.spring.pawple.app.user.UserDtoWithoutPass;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;
import com.project.spring.pawple.app.user.UserService;
import com.project.spring.pawple.app.user.UserSimpleInfoDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


// 관리자 Controller 
@RestController
@RequestMapping("/admin")

// @RequestMapping("/api")
public class ManagerController {
    
    private final UserService userService;
    private final PostService postService;
    private final PostManagerService postManagerService;
    @Autowired
    private final ReportService reportService;
    private final ReviewService reviewService;

    private final PostRepository postRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private final UserRepository userRepository;

    public ManagerController(UserService userService, PostService postService, PostManagerService postManagerService,
                             PostRepository postRepository,ProductService productService, OrderRepository orderRepository,
                             ReportService reportService, UserRepository userRepository,ReviewRepository reviewRepository,ReviewService reviewService){
        this.userService = userService;
        this.postService = postService;
        this.postManagerService = postManagerService;
        this.postRepository = postRepository;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
    }

    // 전체 회원 조회
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDtoWithoutPass>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithoutPass());
    }


    // 펫 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{id}/pets")
    public ResponseEntity<List<PetShowing>> getUserPets(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id); // orElseThrow()
        List<PetShowing> pets = user.getPets().stream()
                               .map(PetShowing::new)  // PetEntity -> PetShowing DTO 변환
                                .collect(Collectors.toList());
        return ResponseEntity.ok(pets);
        }

    // ✅ 이름으로 회원 검색 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/name")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUserByName(@RequestParam String name){
        
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    // ✅ 이메일로 회원 검색 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/email")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUsersByEmail(email));
    }

    // ✅ 소셜 이름으로 회원 검색 (관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/social")
    public ResponseEntity<List<UserDtoWithoutPass>> searchUsersBySocialName(@RequestParam String socialName) {
        return ResponseEntity.ok(userService.searchUsersBySocialName(socialName));
    }



    


    

    // 전체 게시글 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> postDtoList = postManagerService.getAllPost().stream()
            .map(post -> {
                if (Boolean.FALSE.equals(post.getIsPublic())) {
                    return PostDto.blinded(post.getId());
                } else {
                    return PostDto.fromEntity(post);
                }
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(postDtoList);
}


    // vet 접근 
    @PreAuthorize("hasRole('vet')")
    @GetMapping("/vet-only")
    public ResponseEntity<String> vetOnlyAccess(){
        return ResponseEntity.ok("수의사만 접근 가능한 리소스 입니다.");
    }



    // // 전체 게시글 조회 (페이징)
    // @GetMapping("/post")
    // public ResponseEntity<Page<PostDto>> getAllPosts(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int size) {
    //     return ResponseEntity.ok(postService.getAllPosts(page, size));
    // }

    // 게시글 상세 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // 제목으로 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post/search/title")
    public List<PostDto> searchByTitle(@RequestParam String title) {
        List<PostEntity> posts = postManagerService.searchByTitle(title);
        return posts.stream().map(PostDto::fromEntity).collect(Collectors.toList());
    }

    // UserName으로 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post/search/user")
    public List<PostDto> searchByUser(@RequestParam String user) {
        List<PostEntity> posts = postManagerService.searchByUser(user);
        return posts.stream().map(PostDto::fromEntity).collect(Collectors.toList());
    }

    // 내용으로 검색
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(params = "content")
    public ResponseEntity<Page<PostDto>> searchByContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPostsByContent(content, page, size));
    }

    // 제목 또는 내용으로 검색
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(params = "keyword")
    public ResponseEntity<Page<PostDto>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(keyword, page, size));
    }

    // 사용자 이름으로 게시글 검색
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(params = "username")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByUser(username, page, size));
    }

    // // 게시글 삭제 기능
    // @DeleteMapping("/post/delete/{id}")
    // public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    //     postManagerService.deletePostById(id);
    // return ResponseEntity.noContent().build();


    // 게시글 수정 (관리자도 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/post/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto dto,
            @RequestParam String username) {
        return ResponseEntity.ok(postService.updatePost(id, dto, username));
    }

    // 게시글 제목 수정 (PATCH)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/post/update/{id}")
    public ResponseEntity<PostDto> updatePostTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        
        String newTitle = updates.get("title");
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PostEntity updatedEntity = postManagerService.updatePostTitle(id, newTitle);
        if (updatedEntity == null) {
            return ResponseEntity.notFound().build();
        }

        PostDto updatedDto = PostDto.fromEntity(updatedEntity);
        return ResponseEntity.ok(updatedDto);
    }

    // // 게시글 공지로 이동
    // @PreAuthorize("hasRole('ADMIN')")
    // @PatchMapping("/post/move/{id}")
    // public ResponseEntity<PostDto> moveToNotice(@PathVariable Long id) {
    //     PostEntity updatedPost = postManagerService.setPostAsNotice(id);
    //     if (updatedPost == null) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     return ResponseEntity.ok(PostDto.fromEntity(updatedPost));
    // }


    //  // 회원 삭제 API
    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("/user/{id}")
    // public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    //     boolean deleted = userService.deleteUserById(id);
    //     if (deleted) {
    //          return ResponseEntity.noContent().build(); // 204 No Content
    //     } else {
    //          return ResponseEntity.notFound().build(); // 404 Not Found
    //     }
    // }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("사용자 삭제 완료");
    }



    // @DeleteMapping("/user/{id}")
    // public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    //     userService.deleteUser(id);
    //     return ResponseEntity.ok("사용자 삭제 완료");
    // }


    // 게시글, 회원 수 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        long userCount = userService.getUserCount();
        long postCount = postService.getPostCount();

        Map<String, Long> result = new HashMap<>();
        result.put("userCount", userCount);
        result.put("postCount", postCount);

        return ResponseEntity.ok(result);
    }


    // 게시글 전체 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/post/delete/{postId}")
    public ResponseEntity<?> deleteByPost(@PathVariable Long postId) {
        postManagerService.deletePostWithComments(postId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    // 권한 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user/roles/{id}")
    public ResponseEntity<UserDto> updateUserRoles(
        @PathVariable Long id,
        @RequestBody RoleUpdateRequest updateData
    ) {
        List<String> roles = updateData.getRoles();
    
        if (roles == null || roles.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
    
        UserDto updatedUser = userService.updateRoles(id, roles);
        return ResponseEntity.ok(updatedUser);
    }


    // 게시글 공개/비공개 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/post/visibility/{id}")
    public ResponseEntity<PostDto> updateVisibility(
        @PathVariable Long id,
        @RequestBody VisibilityUpdateRequest request) {

        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        post.setIsPublic(request.getIsPublic());
        PostEntity updatedPost = postRepository.save(post);

        PostDto dto = new PostDto();
        dto.setId(updatedPost.getId());
        dto.setTitle(updatedPost.getTitle());
        dto.setContent(updatedPost.getContent());
        dto.setIsPublic(updatedPost.getIsPublic());
        // 필요하다면 더 필드 추가

        return ResponseEntity.ok(dto);
    }


    // 게시글 공개 수정
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<VisibilityUpdateRequest>> getAllPosts(Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<PostEntity> posts = isAdmin ?
                postRepository.findAll() :
                postRepository.findByIsPublicTrue();

        List<VisibilityUpdateRequest> result = posts.stream()
                .map(p -> {
                    VisibilityUpdateRequest dto = new VisibilityUpdateRequest();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    dto.setContent(p.getContent());
                    dto.setIsPublic(p.getIsPublic());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    // 상세 정보 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    public UserSimpleInfoDto getUserSimpleInfoDto(@PathVariable Long id){
        return userService.getUserSimpleInfoDto(id);
    }


    // 게시글 카테고리 이동
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/post/move/{id}")
    public ResponseEntity<PostDto> movePostCategory(
        @PathVariable Long id,
        @RequestParam String category,
        @RequestParam(required = false) String subCategory) {

    PostEntity updatedPost = postManagerService.movePostCategory(id, category, subCategory);
    if (updatedPost == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(PostDto.fromEntity(updatedPost));
    }
    

    // 상품 목록 반환
    @GetMapping("/product{id}")
    public List<ProductEntity> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping("/product/{id}")
    public ProductEntity createProduct(@RequestBody ProductEntity product) {
        return productService.save(product);
    }

    @PatchMapping("/product/{id}")
    public ResponseEntity<ProductEntity> patchProduct(
            @PathVariable Long id,
            @RequestPart("data") ProductEntity patchData,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductEntity updated = productService.patchUpdate(id, patchData, image);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<List<MonthlySalesDto>> getMonthlySales() {
        List<Object[]> raw = orderRepository.findMonthlySales();

        List<MonthlySalesDto> result = raw.stream()
            .map(r -> {
                try {
                    String month = Objects.toString(r[0], "Unknown");
                    Long total = r[1] != null ? ((Number) r[1]).longValue() : 0L;
                    return new MonthlySalesDto(month, total);
                } catch (Exception e) {
                    return new MonthlySalesDto("오류", 0L);
                }
            })
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sales/total")
    public ResponseEntity<Long> getTotalSales() {
        Long total = orderRepository.findTotalSales();
        return ResponseEntity.ok(total != null ? total : 0L); // null 방지
    }

    
    @ResponseBody
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDto>> getAllReports() {

         List<ReportEntity> reports = reportRepository.findAll();
         
        List<ReportDto> dtos = reports.stream().map(report -> {
            String reporterName = userRepository.findById(report.getReporterId())
                    .map(UserEntity::getName)
                    .orElse("탈퇴한 사용자");

            String reportedUserName = userRepository.findById(report.getReportedUserId())
                    .map(UserEntity::getName)
                    .orElse("탈퇴한 사용자");

            return ReportDto.builder()
                    .id(report.getId())
                    .reporterId(report.getReporterId())
                    .reporterName(reporterName)
                    .reportedUserId(report.getReportedUserId())
                    .reportedUserName(reportedUserName)
                    .reason(report.getReason())
                    .targetType(report.getTargetType())
                    .commentId(report.getCommentId())
                    .postId(report.getPostId())
                    .status(report.getStatus())
                    .reportedAt(report.getReportedAt())
                    .build();
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/reports/{id}")
    public ResponseEntity<?> updateReportStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> updateData
    ) {
        Optional<ReportEntity> optionalReport = reportRepository.findById(id);
        if (!optionalReport.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 신고가 존재하지 않습니다.");
        }

        ReportEntity report = optionalReport.get();
        String newStatus = updateData.get("status");
        report.setStatus(newStatus);
        reportRepository.save(report);

        return ResponseEntity.ok("상태가 업데이트되었습니다.");
    }


    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
    if (!reportRepository.existsById(id)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 신고가 존재하지 않습니다.");
    }

    reportRepository.deleteById(id);
    return ResponseEntity.ok("신고가 삭제되었습니다.");
}

    @PatchMapping("/reports/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody
    ) {
        String newStatus = requestBody.get("status");
        reportService.updateStatus(id, newStatus);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    // review 전체 조회
    @GetMapping("/review")
    public List<ReviewDto> getAllReviewsWithoutImage() {
        return reviewRepository.findAll().stream()
                .map(ReviewDto::fromEntity)
                .peek(dto -> dto.setImage(null)) // 이미지 null 처리
                .collect(Collectors.toList());
    }

    // review 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }

    
    // 특정 상품의 모든 리뷰 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/review/product/")
    public List<ReviewEntity> findByProductId(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }
    
    // review 공개 여부 수정
     @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/review/{id}/public")
    public ReviewDto updateReviewVisibility(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String isPublic = request.get("isPublic");
        ReviewEntity updated = reviewService.updateVisibility(id, isPublic);
        return ReviewDto.fromEntity(updated);
    }

    // 본문 조회(보류)




}









    

    
