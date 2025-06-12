package com.project.spring.pawple.app.comment;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;
    private String userThumbnailUrl;
    private String userImageUrl;
    private Long postId;
    private Long parentId; // 대댓글일 경우 부모 댓글 ID
    private List<CommentDto> children; // 응답 시 자식 댓글 포함
    private Long likeCount;
}