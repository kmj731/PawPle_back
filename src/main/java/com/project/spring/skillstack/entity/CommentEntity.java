package com.project.spring.skillstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE, 
        generator = "CommentSeq"
    )
    @SequenceGenerator(
        name = "CommentSeq", 
        sequenceName = "CommentSeq", 
        allocationSize = 1
    )
    
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String content;

    private String writer;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
