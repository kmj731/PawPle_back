package com.project.spring.pawple.app.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // 특정 상품에 대한 리뷰 목록 조회
    List<ReviewEntity> findByProduct_Id(Long productId);
}