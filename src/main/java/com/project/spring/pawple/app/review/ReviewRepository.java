package com.project.spring.pawple.app.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // 특정 상품에 대한 리뷰 목록 조회
    List<ReviewEntity> findByProduct_Id(Long productId);

    // 공개된 리뷰만 조회
    List<ReviewEntity> findByProduct_IdAndIsPublic(Long productId, String isPublic);


    @Query("SELECT r FROM ReviewEntity r LEFT JOIN FETCH r.product")
    List<ReviewEntity> findAllWithProduct();
}