package com.project.spring.skillstack.dto;

import com.project.spring.skillstack.entity.ConsultEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultDto {

    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "상담 주제를 선택해주세요.")
    private String subCategory;

    private String status;

    private Long userId;
    private String username;

    @NotNull(message = "반려동물을 선택해주세요.")
    private Long petId;

    private String petName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String replyContent;
    private String replyAuthor;
    private LocalDateTime replyCreatedAt;

    public static ConsultDto fromEntity(ConsultEntity entity) {
        return ConsultDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .subCategory(entity.getSubCategory())
                .status(entity.getStatus())
                .userId(entity.getUser().getId())
                .username(entity.getUser().getName())
                .petId(entity.getPet() != null ? entity.getPet().getId() : null)
                .petName(entity.getPet() != null ? entity.getPet().getPetName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .replyContent(entity.getReplyContent())
                .replyAuthor(entity.getReplyAuthor())
                .replyCreatedAt(entity.getReplyCreatedAt())
                .build();
    }

    public ConsultEntity toEntity() {
        return ConsultEntity.builder()
                .title(this.title)
                .content(this.content)
                .subCategory(this.subCategory)
                .status(this.status != null ? this.status.toUpperCase() : "PENDING")
                .replyContent(this.replyContent)
                .replyAuthor(this.replyAuthor)
                .replyCreatedAt(this.replyCreatedAt)
                .build();
    }
}

