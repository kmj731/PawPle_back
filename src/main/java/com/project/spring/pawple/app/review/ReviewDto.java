package com.project.spring.pawple.app.review;

import com.project.spring.pawple.app.store.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private Long productId;
    private Long userId;
    private String nickname;
    private String isPublic;
    private String content;
    private Integer rating;
    private String image;
    private String createdAt;

    public static ReviewDto fromEntity(ReviewEntity entity) {
        return ReviewDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .isPublic(entity.getIsPublic())
                .content(entity.getContent())
                .rating(entity.getRating())
                .image(entity.getImage())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ReviewEntity toEntity() {
        return ReviewEntity.builder()
                .id(id)
                .product(ProductEntity.builder().id(productId).build()) // 단방향 연결
                .userId(userId)
                .nickname(nickname)
                .isPublic(isPublic)
                .content(content)
                .rating(rating)
                .image(image)
                .createdAt(createdAt)
                .build();
    }
}