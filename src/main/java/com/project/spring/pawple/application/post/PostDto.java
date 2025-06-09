package com.project.spring.pawple.application.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.project.spring.pawple.application.media.MediaDto;
import com.project.spring.pawple.application.pet.PetDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    
    private Long id;    
    private String title;
    private String content;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Boolean isLiked; // 현재 사용자 좋아요 상태
    private String category;
    private String subCategory;
    private Boolean isPublic;
    private Long petId; // 클라이언트에서 보낼 반려동물 ID
    private PetDto pet;
    private List<MediaDto> mediaList;

    
    // 요청 데이터용 생성자
    public PostDto(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
    
    // 엔티티를 DTO로 변환
    public static PostDto fromEntity(PostEntity entity) {
        return PostDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorName(entity.getUser().getName())
                .authorId(entity.getUser().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .viewCount(entity.getViewCount())
                .commentCount(entity.getCommentCount())
                .likeCount(entity.getLikeCount())
                .category(entity.getCategory())
                .subCategory(entity.getSubCategory())
                .isPublic(entity.getIsPublic())
                .pet(entity.getPet() != null ? PetDto.fromEntity(entity.getPet()) : null)
                .petId(entity.getPet() != null ? entity.getPet().getId() : null)
                .mediaList(
                    entity.getMediaList().stream()
                    .map(MediaDto::fromEntity)
                    .collect(Collectors.toList())
                )
                .build();
    }
    
    // DTO를 엔티티로 변환 (생성용)
    public PostEntity toEntity() {
        return PostEntity.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .subCategory(this.subCategory)
                .isPublic(this.isPublic != null ? this.isPublic : true)
                .build();
    }

    // 블라인드 처리용 팩토리
    public static PostDto blinded(Long postId) {
        return PostDto.builder()
                .id(postId)
                .title("블라인드 처리된 게시글입니다.")
                .content("관리자에 의해 비공개 처리된 게시글입니다.")
                .authorName("비공개")
                .isPublic(false)
                .build();
    }
}