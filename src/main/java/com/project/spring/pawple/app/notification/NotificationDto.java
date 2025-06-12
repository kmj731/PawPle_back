package com.project.spring.pawple.app.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    private Long postId;      // 게시글 ID
    private String postTitle; // 게시글 제목 
}
