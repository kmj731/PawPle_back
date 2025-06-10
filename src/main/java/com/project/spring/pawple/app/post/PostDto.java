package com.project.spring.pawple.app.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.project.spring.pawple.app.media.MediaDto;
import com.project.spring.pawple.app.pet.PetDto;

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
    private Boolean isLiked;
    private String category;
    private String subCategory;
    private Boolean isPublic;
    private Long petId;
    private PetDto pet;
    private List<MediaDto> mediaList;
    private Boolean isNew; // 추가된 필드

    public PostDto(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;     
    }

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
                .isNew(entity.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)))
                .build();
    }

    public PostEntity toEntity() {
        return PostEntity.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .subCategory(this.subCategory)
                .isPublic(this.isPublic != null ? this.isPublic : true)
                .build();
    }

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
