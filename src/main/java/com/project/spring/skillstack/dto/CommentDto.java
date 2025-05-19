package com.project.spring.skillstack.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Long postId;
    private String content;
    private String writer;
}

