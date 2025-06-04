package com.project.spring.skillstack.entity;



import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class PointLog {
    
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private String reason;
    private int amount;
    private LocalDateTime createAt;

        @PrePersist
        protected void onCreate(){
            this.createAt = LocalDateTime.now();
        }
}
