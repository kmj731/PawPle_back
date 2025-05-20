package com.project.spring.skillstack.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_table")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
    allocationSize = 1,
    initialValue = 1,
    name = "PostSeq",
    sequenceName = "PostSeq"
)
public class PostEntity {
    
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "PostSeq"
    )
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column
    private Integer viewCount;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    // 카테고리 추가 (반려동물 건강 관련 커뮤니티이므로 카테고리 유용)
    @Column(length = 50)
    private String category;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}