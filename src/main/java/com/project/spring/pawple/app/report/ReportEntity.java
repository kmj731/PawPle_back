package com.project.spring.pawple.app.report;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long reporterId;
    private Long reportedUserId;
    private Long postId;
    private Long commentId;
    private String reason;
    private String targetType;
    private LocalDateTime reportedAt;
    private String status;
}

