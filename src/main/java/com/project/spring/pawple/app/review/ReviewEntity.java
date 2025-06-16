package com.project.spring.pawple.app.review;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.project.spring.pawple.app.store.ProductEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "REVIEW_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
    name = "ReviewSeq",
    sequenceName = "ReviewSeq",
    initialValue = 1,
    allocationSize = 1
)
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReviewSeq")
    private Long id;

    private Long productId; // 어떤 상품에 대한 리뷰인지 (FK처럼 사용)

    private Long userId;    // 누가 작성했는지 (사용자 ID, 추후 회원 기능 연동 대비)

    private String nickname; // 작성자 닉네임

    private String isPublic; // 공개 여부

    private String content;  // 리뷰 본문

    private Integer rating;  // 평점 (예: 1~5)

    private String image;    // 이미지 URL (선택사항)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_paw_id")
    private ProductEntity product;

}
