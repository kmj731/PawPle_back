package com.project.spring.skillstack.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writer; // writer의 이름 또는 username
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments; // 댓글 목록 포함
}
