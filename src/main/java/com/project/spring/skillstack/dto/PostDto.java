package com.project.spring.skillstack.dto;

import java.time.LocalDateTime;

import com.project.spring.skillstack.entity.PostEntity;

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
    private String category;
    private Boolean isPublic;
    
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
                .category(entity.getCategory())
                .isPublic(entity.getIsPublic())
                .build();
    }
    
    // DTO를 엔티티로 변환 (생성용)
    public PostEntity toEntity() {
        return PostEntity.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .isPublic(this.isPublic != null ? this.isPublic : true)
                .build();
    }
}