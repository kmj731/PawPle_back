package com.project.spring.pawple.app.point;

import java.time.LocalDateTime;

import com.project.spring.pawple.app.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "POINT_LOG")
@SequenceGenerator(
    allocationSize = 1,
    initialValue = 1,
    name = "PointLogSeq",
    sequenceName = "PointLogSeq"
)
public class PointLog {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "PointLogSeq"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String reason;

    // 포인트 적립/차감 구분 (예: "EARN", "USE")
    @Column(nullable = false)
    private String type;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
    this.created = LocalDateTime.now();
    this.createdAt = this.created; // 두 컬럼에 동일 시간 입력
    }

}